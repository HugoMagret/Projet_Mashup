package org.example;

import org.example.internal.InternalLeadDTO;
import org.example.internal.ThriftNoSuchLeadException;
import org.example.internal.ThriftWrongOrderForRevenueException;
import org.example.internal.ThriftWrongStateException;
import org.apache.thrift.TException;

import java.util.List;

/**
 * Service de "merge" des Leads Salesforce -> InternalCRM.
 *
 * C'est cette classe dont tu parleras dans le rapport (diagramme de séquence
 * sur la méthode mergeLeads).
 */
public class LeadMerger {

    private final InternalCRMThriftClient internalClient;
    private final SalesforceLeadProvider salesforceProvider;

    public LeadMerger(InternalCRMThriftClient internalClient,
                      SalesforceLeadProvider salesforceProvider) {
        this.internalClient = internalClient;
        this.salesforceProvider = salesforceProvider;
    }

    /**
     * Merge principal : récupère tous les leads Salesforce, et les ajoute
     * dans InternalCRM via createLead().
     *
     * @param clearInternalFirst si true, on supprime d'abord tous les leads
     *                           présents dans InternalCRM (en utilisant deleteLead()).
     */
    public void mergeLeads(boolean clearInternalFirst) throws Exception {
        if (clearInternalFirst) {
            clearInternalCRM();
        }

        System.out.println("Récupération des leads Salesforce...");
        List<InternalLeadDTO> sfLeads = salesforceProvider.fetchAllLeadsAsInternalDto();
        System.out.println("Leads Salesforce à importer : " + sfLeads.size());

        int created = 0;
        int failed = 0;
        for (InternalLeadDTO lead : sfLeads) {
            try {
                long id = internalClient.createLead(lead);
                created++;
                if (created % 5 == 0) {
                    System.out.println("  Progression : " + created + "/" + sfLeads.size() + " leads créés...");
                }
            } catch (Exception e) {
                failed++;
                System.err.println("  ERREUR lors de la création du lead " + lead.getFirstName() + " " + lead.getLastName() + " : " + e.getMessage());
                if (failed > 5) {
                    System.err.println("  Trop d'erreurs, arrêt de l'import");
                    break;
                }
            }
        }

        System.out.println("Import terminé. Leads créés : " + created + ", échecs : " + failed);
    }

    /**
     * Supprime tous les leads présents dans InternalCRM.
     * Utilise findAllLeads() puis deleteLead() pour chacun.
     */
    private void clearInternalCRM() throws ThriftWrongOrderForRevenueException,
            ThriftWrongStateException,
            ThriftNoSuchLeadException,
            TException {
        System.out.println("Nettoyage : suppression de tous les leads InternalCRM...");
        List<InternalLeadDTO> existing = internalClient.findAllLeads();
        System.out.println("Leads trouvés dans InternalCRM : " + existing.size());

        int deleted = 0;
        for (InternalLeadDTO lead : existing) {
            // deleteLead s'appuie sur un "template" complet, donc on lui passe tel quel
            internalClient.deleteLead(lead);
            deleted++;
        }
        System.out.println("Leads supprimés : " + deleted);
    }

    /**
     * Création d'un lead individuel (utile pour la commande 'add').
     */
    public long addLead(InternalLeadDTO lead) throws Exception {
        try {
            return internalClient.createLead(lead);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du lead : " + e.getMessage());
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                System.err.println("  Le serveur InternalCRM n'a pas répondu dans les temps.");
                System.err.println("  Vérifiez que le serveur est bien démarré et fonctionne correctement.");
            }
            throw e;
        }
    }

    /**
     * Suppression d'un lead individuel (utile pour la commande 'delete').
     * On utilise le matching exact de InternalCRM (equalsWithoutId côté modèle).
     */
    public void deleteLead(InternalLeadDTO template) throws Exception {
        try {
            internalClient.deleteLead(template);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du lead : " + e.getMessage());
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                System.err.println("  Le serveur InternalCRM n'a pas répondu dans les temps.");
                System.err.println("  Vérifiez que le serveur est bien démarré et fonctionne correctement.");
            }
            throw e;
        }
    }
}

