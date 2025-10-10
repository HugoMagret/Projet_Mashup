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
        
        // Ajouter un nouveau prospect pour tester
        InternalLeadDTO nouveauProspect = new InternalLeadDTO();
        nouveauProspect.setFirstName("Alice");
        nouveauProspect.setLastName("Martin");
        nouveauProspect.setAnnualRevenue(120000);  // 120k€
        nouveauProspect.setState("Loire-Atlantique");
        handler.createLead(nouveauProspect);

        // Chercher tous les prospects entre 100k€ et 130k€ en Loire-Atlantique
        List<InternalLeadDTO> resultats = handler.findLeads(100000, 130000, "Loire-Atlantique");
        
        // Afficher les résultats
        System.out.println("[DEMO] Prospects trouvés (100k-130k€, Loire-Atlantique) = " + resultats.size());
        for (InternalLeadDTO prospect : resultats) {
            System.out.println("  -> " + prospect.getFirstName());  // Sera au format "Nom, Prénom"
        }

        System.out.println("[DEMO] Test terminé avec succès !");
    }
}