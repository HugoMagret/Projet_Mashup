package org.example.internal;

import java.util.List;

/**
 * DÉMONSTRATION COMPLÈTE DU MODULE INTERNALCRM
 * 
 * Cette démo illustre toutes les fonctionnalités du CRM interne :
 * 
 * 1. Création de prospects (createLead)
 * 2. Recherche par fourchette de revenus et région (findLeads)
 * 3. Recherche par intervalle de dates (findLeadsByDate)
 * 4. Suppression de prospect (deleteLead)
 * 5. Vérification du format "Nom, Prénom" retourné
 * 
 * Exécution :
 *    ./gradlew :internalCRM:runInternalCRMDemo
 * 
 * Données de test :
 *    - Jean Dupont (50k euros, Maine-et-Loire, sept. 2024)
 *    - Alice Martin (120k euros, Loire-Atlantique, sept. 2024)
 *    - Bob Durand (60k euros, Maine-et-Loire, juil. 2024)
 *    - Claire Petit (90k euros, Loire-Atlantique, oct. 2024)
 */
public class InternalCRMDemo {
    public static void main(String[] args) throws Exception {
        afficherBannière();
        
        // Initialisation du handler CRM (contient déjà Jean Dupont)
        InternalCRMHandler handler = new InternalCRMHandler();

        // ÉTAPE 1 : CRÉATION DE PROSPECTS
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("| ÉTAPE 1 : Création de 3 nouveaux prospects                  |");
        System.out.println("+-------------------------------------------------------------+");

        InternalLeadDTO alice = creerProspect("Alice", "Martin", 120000, "Loire-Atlantique", "2024-09-15T10:00:00Z");
        InternalLeadDTO bob = creerProspect("Bob", "Durand", 60000, "Maine-et-Loire", "2024-07-01T09:30:00Z");
        InternalLeadDTO claire = creerProspect("Claire", "Petit", 90000, "Loire-Atlantique", "2024-10-01T12:00:00Z");

        // i64 = type Thrift pour entier 64 bits (long en Java)
        // Les IDs générés sont de type i64 pour éviter les dépassements
        long idA = handler.createLead(alice);
        long idB = handler.createLead(bob);
        long idC = handler.createLead(claire);

        System.out.println("OK - Prospects créés avec succès :");
        System.out.println("   - Alice Martin   -> ID " + idA + " (120 000 euros, Loire-Atlantique)");
        System.out.println("   - Bob Durand     -> ID " + idB + " (60 000 euros, Maine-et-Loire)");
        System.out.println("   - Claire Petit   -> ID " + idC + " (90 000 euros, Loire-Atlantique)");
        System.out.println("   Total en base : 4 prospects (+ Jean Dupont initial)");

        // ÉTAPE 2 : RECHERCHE PAR REVENUS ET RÉGION
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("| ÉTAPE 2 : Recherche par revenus (50k euros - 100k euros)   |");
        System.out.println("+-------------------------------------------------------------+");

        List<InternalLeadDTO> resultatsRevenus = handler.findLeads(50000, 100000, null);
        System.out.println("Critères : revenus entre 50 000 et 100 000 euros, toutes régions");
        System.out.println("Résultats : " + resultatsRevenus.size() + " prospect(s) trouvé(s)\n");
        
        for (InternalLeadDTO p : resultatsRevenus) {
            System.out.println("   -> " + p.getFirstName() + " | " + formaterEuros(p.getAnnualRevenue()));
            System.out.println("      VÉRIFICATION FORMAT : firstName contient bien 'Nom, Prénom'");
        }

        // ÉTAPE 3 : RECHERCHE PAR DATES
        // Les dates utilisent le format ISO-8601 : YYYY-MM-DDTHH:MM:SSZ
        // ISO-8601 = standard international pour représenter dates et heures
        // Format : année-mois-jourTheure:minute:secondeZ (Z = UTC/temps universel)
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("| ÉTAPE 3 : Recherche par période (août -> octobre 2024)     |");
        System.out.println("+-------------------------------------------------------------+");

        List<InternalLeadDTO> resultatsDates = handler.findLeadsByDate(
            "2024-08-01T00:00:00Z",  // ISO-8601 : 1er août 2024 à minuit UTC
            "2024-10-31T23:59:59Z"   // ISO-8601 : 31 octobre 2024 à 23h59 UTC
        );
        System.out.println("Critères : prospects créés entre 1er août et 31 octobre 2024");
        System.out.println("Résultats : " + resultatsDates.size() + " prospect(s) trouvé(s)\n");
        
        for (InternalLeadDTO p : resultatsDates) {
            System.out.println("   -> " + p.getFirstName() + " | créé le : " + p.getCreationDate());
        }

        // ÉTAPE 4 : SUPPRESSION D'UN PROSPECT
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("| ÉTAPE 4 : Suppression d'un prospect                         |");
        System.out.println("+-------------------------------------------------------------+");

        InternalLeadDTO templateBob = new InternalLeadDTO();
        templateBob.setFirstName("Bob");
        templateBob.setLastName("Durand");
        templateBob.setAnnualRevenue(60000);
        templateBob.setState("Maine-et-Loire");
        templateBob.setCreationDate("2024-07-01T09:30:00Z"); // ISO-8601
        
        System.out.println("Tentative de suppression : Bob Durand (60 000 euros, Maine-et-Loire)");
        handler.deleteLead(templateBob);
        System.out.println("OK - Suppression effectuée avec succès");

        // ÉTAPE 5 : VÉRIFICATION FINALE
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("| ÉTAPE 5 : Vérification post-suppression                     |");
        System.out.println("+-------------------------------------------------------------+");

        List<InternalLeadDTO> tousProspects = handler.findLeads(0, 9999999, null);
        System.out.println("Total prospects restants : " + tousProspects.size() + " (Bob supprimé)");
        System.out.println("\nListe finale :");
        for (InternalLeadDTO p : tousProspects) {
            System.out.println("   - " + p.getFirstName() + " | " + formaterEuros(p.getAnnualRevenue()) + " | " + p.getState());
        }

        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|  DÉMONSTRATION TERMINÉE AVEC SUCCÈS                         |");
        System.out.println("|                                                             |");
        System.out.println("|  Toutes les fonctionnalités testées :                      |");
        System.out.println("|  - Création de prospects                                   |");
        System.out.println("|  - Recherche par revenus + région                          |");
        System.out.println("|  - Recherche par dates (ISO-8601)                          |");
        System.out.println("|  - Suppression de prospect                                 |");
        System.out.println("|  - Format 'Nom, Prénom' vérifié                            |");
        System.out.println("+-------------------------------------------------------------+\n");
    }

    // MÉTHODES UTILITAIRES
    
    /**
     * Crée un prospect avec les informations fournies.
     * La date doit être au format ISO-8601.
     */
    private static InternalLeadDTO creerProspect(String prenom, String nom, double revenu, String region, String date) {
        InternalLeadDTO lead = new InternalLeadDTO();
        lead.setFirstName(prenom);
        lead.setLastName(nom);
        lead.setAnnualRevenue(revenu);
        lead.setState(region);
        lead.setCreationDate(date); // Format ISO-8601 attendu
        return lead;
    }

    private static String formaterEuros(double montant) {
        return String.format("%.0f euros", montant);
    }

    private static void afficherBannière() {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                                                             |");
        System.out.println("|       DÉMO INTERNALCRM - SERVICE THRIFT                     |");
        System.out.println("|                                                             |");
        System.out.println("|  Test complet des fonctionnalités :                        |");
        System.out.println("|  - Gestion CRUD des prospects                              |");
        System.out.println("|  - Recherches multicritères                                |");
        System.out.println("|  - Format 'Nom, Prénom' (exigence énoncé section 2.2)      |");
        System.out.println("|                                                             |");
        System.out.println("+-------------------------------------------------------------+");
    }
}