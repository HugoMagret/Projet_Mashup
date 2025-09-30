package org.example.internal;

import java.util.List;

/**
 * Démo rapide pour vérifier le fonctionnement du handler sans client Thrift.
 * Usage: ./gradlew :internalCRM:runInternalCRMDemo
 */
public class InternalCRMDemo {
    public static void main(String[] args) throws Exception {
        InternalCRMHandler handler = new InternalCRMHandler();
        // Création d'un lead supplémentaire
        InternalLeadDTO lead = new InternalLeadDTO();
        lead.setFirstName("Alice");
        lead.setLastName("Martin");
        lead.setAnnualRevenue(120000);
        lead.setState("Loire-Atlantique");
        handler.createLead(lead);

        List<InternalLeadDTO> list = handler.findLeads(100000, 130000, "Loire-Atlantique");
        System.out.println("[DEMO] Leads filtrés 100k-130k Loire-Atlantique = " + list.size());
        for (InternalLeadDTO l : list) {
            System.out.println("  -> " + l.getFirstName());
        }

        System.out.println("[DEMO] OK");
    }
}