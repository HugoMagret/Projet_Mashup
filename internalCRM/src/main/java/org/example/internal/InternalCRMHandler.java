package org.example.internal;

import java.util.*;

/**
 * CRM INTERNE : stocke et filtre les prospects commerciaux en mémoire
 * 
 * QUE FAIT CE SERVICE :
 * - Garde une liste de prospects (nom, entreprise, chiffre d'affaires,
 * région...)
 * - Trouve les prospects par critères (revenus entre X et Y, région donnée)
 * - Trouve les prospects par date de création
 * - Ajoute/supprime des prospects
 * 
 * EXEMPLE D'USAGE :
 * findLeads(50000, 150000, "Loire-Atlantique")
 * → tous les prospects entre 50k€ et 150k€ en Loire-Atlantique
 */
public class InternalCRMHandler implements InternalCRM.Iface {
    /**
     * Handler utilisé par le serveur et la demo.
     * Il délègue au modèle métier et convertit/propage les exceptions Thrift.
     */

    // Déléguer vers le modèle métier centralisé
    private final org.example.internal.model.LeadModel model = org.example.internal.model.LeadModelFactory.getModel();

    public InternalCRMHandler() {
        // Charger les données initiales (environ 50 prospects)
        List<InternalLeadDTO> prospectsInitiaux = InitialDataLoader.genererProspectsInitiaux();

        System.out.println("[InternalCRM] Chargement de " + prospectsInitiaux.size() + " prospects initiaux...");
        int compteur = 0;

        for (InternalLeadDTO prospect : prospectsInitiaux) {
            try {
                createLead(prospect);
                compteur++;
            } catch (ThriftWrongStateException e) {
                // Log l'erreur mais continue le chargement
                System.err.println("[ERREUR] Impossible de créer le prospect " +
                        prospect.getFirstName() + " " + prospect.getLastName() +
                        " : État invalide - " + e.getMessage());
            } catch (Exception e) {
                // Capture toute autre exception inattendue
                System.err.println("[ERREUR] Erreur inattendue lors de la création du prospect " +
                        prospect.getFirstName() + " " + prospect.getLastName() +
                        " : " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        System.out.println("[InternalCRM] ✓ " + compteur + "/" + prospectsInitiaux.size() +
                " prospects chargés avec succès");
    }

    @Override
    public List<org.example.internal.InternalLeadDTO> findLeads(double lowAnnualRevenue, double highAnnualRevenue,
            String province)
            throws ThriftWrongOrderForRevenueException, ThriftWrongStateException {
        // Appel direct au modèle. Les erreurs sont converties en exceptions Thrift.
        try {
            List<org.example.internal.model.Lead> leads = model.findLeads(lowAnnualRevenue, highAnnualRevenue,
                    province);
            return org.example.internal.utils.ConverterUtils.toDtoList(leads);
        } catch (org.example.internal.model.exception.WrongOrderForRevenueException e) {
            throw new ThriftWrongOrderForRevenueException(e.getMessage());
        } catch (org.example.internal.model.exception.WrongStateException e) {
            throw new ThriftWrongStateException(e.getMessage());
        }
    }

    /**
     * Recherche les prospects crees entre deux dates.
     * 
     * Format ISO-8601 requis pour les dates :
     * - Format : "yyyy-MM-dd'T'HH:mm:ss'Z'"
     * - Exemple : "2024-09-15T10:00:00Z"
     * - Z indique le fuseau UTC (temps universel)
     * 
     * Lance ThriftWrongDateFormatException si le format est invalide.
     * Lance ThriftWrongOrderForDateException si fromIso > toIso.
     */
    @Override
    public List<org.example.internal.InternalLeadDTO> findLeadsByDate(String fromIso, String toIso)
            throws ThriftWrongDateFormatException, ThriftWrongOrderForDateException {
        try {
            java.util.Calendar from = org.example.internal.utils.ConverterUtils.isoStringToCalendar(fromIso);
            java.util.Calendar to = org.example.internal.utils.ConverterUtils.isoStringToCalendar(toIso);
            if (fromIso != null && from == null)
                throw new ThriftWrongDateFormatException("Format de date invalide: " + fromIso);
            if (toIso != null && to == null)
                throw new ThriftWrongDateFormatException("Format de date invalide: " + toIso);
            List<org.example.internal.model.Lead> leads = model.findLeadsByDate(from, to);
            return org.example.internal.utils.ConverterUtils.toDtoList(leads);
        } catch (org.example.internal.model.exception.WrongDateFormatException e) {
            throw new ThriftWrongDateFormatException(e.getMessage());
        } catch (org.example.internal.model.exception.WrongOrderForDateException e) {
            throw new ThriftWrongOrderForDateException(e.getMessage());
        }
    }

    /**
     * Cree un nouveau prospect et retourne son ID unique.
     * 
     * Retour : i64 (long en Java) = entier 64 bits permettant
     * de generer des milliards d'IDs uniques sans depassement
     */
    @Override
    public long createLead(org.example.internal.InternalLeadDTO lead) throws ThriftWrongStateException {
        org.example.internal.model.Lead l = org.example.internal.utils.ConverterUtils.toModel(lead);
        try {
            return model.createLead(l);
        } catch (org.example.internal.model.exception.WrongStateException e) {
            throw new ThriftWrongStateException(e.getMessage());
        }
    }

    @Override
    public void deleteLead(org.example.internal.InternalLeadDTO leadDto) throws ThriftNoSuchLeadException {
        try {
            System.out.println("[InternalCRMHandler] Tentative de suppression d'un lead...");
            System.out.println("[InternalCRMHandler] DTO reçu - firstName='" + leadDto.getFirstName() + 
                             "', lastName='" + leadDto.getLastName() + 
                             "', annualRevenue=" + leadDto.getAnnualRevenue() + 
                             ", isSetAnnualRevenue=" + leadDto.isSetAnnualRevenue());
            org.example.internal.model.Lead template = org.example.internal.utils.ConverterUtils.toModel(leadDto);
            System.out.println("[InternalCRMHandler] Template créé - firstName='" + template.getFirstName() + 
                             "', lastName='" + template.getLastName() + 
                             "', annualRevenue=" + template.getAnnualRevenue());
            model.deleteLead(template);
            System.out.println("[InternalCRMHandler] Lead supprimé avec succès");
        } catch (org.example.internal.model.exception.NoSuchLeadException e) {
            System.out.println("[InternalCRMHandler] Aucun lead correspondant trouvé : " + e.getMessage());
            throw new ThriftNoSuchLeadException(e.getMessage());
        } catch (Exception e) {
            // Capturer toutes les autres exceptions pour éviter "Internal error processing deleteLead"
            System.err.println("[InternalCRMHandler] ERREUR lors de la suppression du lead : " + e.getClass().getSimpleName());
            System.err.println("[InternalCRMHandler] Message : " + e.getMessage());
            e.printStackTrace();
            // Relancer comme ThriftNoSuchLeadException avec un message explicite
            throw new ThriftNoSuchLeadException("Erreur lors de la suppression : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private boolean equalsWithoutId(org.example.internal.model.Lead a, org.example.internal.model.Lead b) {
        if (a == null || b == null)
            return false;
        return safeEq(a.getFirstName(), b.getFirstName()) && safeEq(a.getLastName(), b.getLastName())
                && Double.compare(a.getAnnualRevenue(), b.getAnnualRevenue()) == 0
                && safeEq(a.getPhone(), b.getPhone())
                && safeEq(a.getStreet(), b.getStreet())
                && safeEq(a.getPostalCode(), b.getPostalCode())
                && safeEq(a.getCity(), b.getCity())
                && safeEq(a.getCountry(), b.getCountry())
                && safeCalEq(a.getCreationDate(), b.getCreationDate())
                && safeEq(a.getCompanyName(), b.getCompanyName())
                && safeEq(a.getState(), b.getState());
    }

    private boolean safeEq(String x, String y) {
        if (x == null)
            return y == null;
        return x.equals(y);
    }

    private boolean safeCalEq(java.util.Calendar a, java.util.Calendar b) {
        if (a == null)
            return b == null;
        if (b == null)
            return false;
        return a.getTimeInMillis() == b.getTimeInMillis();
    }
}
