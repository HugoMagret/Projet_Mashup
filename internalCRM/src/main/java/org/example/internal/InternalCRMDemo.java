package org.example.internal;

import java.util.List;

/**
 * DÉMO CRM INTERNE — Exécution locale rapide
 *
 * But : montrer l'utilisation du handler sans serveur réseau.
 * Ce programme :
 * 1) crée un prospect d'exemple,
 * 2) invoque findLeads(100000,130000,"Loire-Atlantique"),
 * 3) affiche le nombre et les noms retournés.
 *
 * Exécution : ./gradlew :internalCRM:runInternalCRMDemo
 */
public class InternalCRMDemo {
    public static void main(String[] args) throws Exception {
        // Créer le gestionnaire CRM (notre "base de données" en mémoire)
        InternalCRMHandler handler = new InternalCRMHandler();

        System.out.println("[DEMO] Début de la démonstration complète du CRM interne");

        // 1) Création de plusieurs prospects avec différentes dates
        InternalLeadDTO a = new InternalLeadDTO();
        a.setFirstName("Alice"); a.setLastName("Martin"); a.setAnnualRevenue(120000); a.setState("Loire-Atlantique");
        a.setCreationDate("2024-09-15T10:00:00Z");

        InternalLeadDTO b = new InternalLeadDTO();
        b.setFirstName("Bob"); b.setLastName("Durand"); b.setAnnualRevenue(60000); b.setState("Maine-et-Loire");
        b.setCreationDate("2024-07-01T09:30:00Z");

        InternalLeadDTO c = new InternalLeadDTO();
        c.setFirstName("Claire"); c.setLastName("Petit"); c.setAnnualRevenue(90000); c.setState("Loire-Atlantique");
        c.setCreationDate("2024-10-01T12:00:00Z");

        long idA = handler.createLead(a);
        long idB = handler.createLead(b);
        long idC = handler.createLead(c);

        System.out.println("[DEMO] Créés IDs = " + idA + ", " + idB + ", " + idC);

        // 2) Recherche par revenus + province
        List<InternalLeadDTO> res1 = handler.findLeads(50000, 100000, null);
        System.out.println("[DEMO] findLeads(50k-100k, null) -> " + res1.size() + " résultats");
        for (InternalLeadDTO p : res1) System.out.println("   -> " + p.getFirstName() + " (" + p.getAnnualRevenue() + ")");

        // 3) Recherche par dates (intervalle)
        List<InternalLeadDTO> res2 = handler.findLeadsByDate("2024-08-01T00:00:00Z", "2024-10-31T23:59:59Z");
        System.out.println("[DEMO] findLeadsByDate(2024-08-01 -> 2024-10-31) -> " + res2.size() + " résultats");
        for (InternalLeadDTO p : res2) System.out.println("   -> " + p.getFirstName() + " (date=" + p.getCreationDate() + ")");

        // 4) Suppression d'un prospect (par template)
        InternalLeadDTO template = new InternalLeadDTO();
        template.setFirstName("Bob"); template.setLastName("Durand"); template.setAnnualRevenue(60000);
        template.setState("Maine-et-Loire"); template.setCreationDate("2024-07-01T09:30:00Z");
        handler.deleteLead(template);
        System.out.println("[DEMO] Suppression effectuée pour Bob Durand");

        // 5) Vérifier suppression
        List<InternalLeadDTO> afterDelete = handler.findLeads(0, 9999999, null);
        System.out.println("[DEMO] Total prospects après suppression = " + afterDelete.size());

        System.out.println("[DEMO] Démo complète terminée.");
    }
}