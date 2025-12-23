package org.example.internal;

import java.util.List;

/**
 * Script de vérification pour tester le chargement des données initiales.
 * 
 * Exécution :
 *   ./gradlew :internalCRM:runVerificationDataLoader
 * 
 * Affiche :
 *   - Nombre total de prospects chargés
 *   - Répartition par région
 *   - Fourchette de revenus
 *   - Premiers et derniers prospects
 */
public class VerificationDataLoader {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     VÉRIFICATION DU CHARGEMENT DES DONNÉES INTERNALCRM       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Étape 1 : Créer le handler (qui charge automatiquement les données)
        System.out.println("═══ ÉTAPE 1 : Initialisation du Handler ═══");
        InternalCRMHandler handler = new InternalCRMHandler();
        System.out.println();

        // Étape 2 : Récupérer TOUS les prospects (revenus de 0 à infini)
        System.out.println("═══ ÉTAPE 2 : Récupération de tous les prospects ═══");
        try {
            List<InternalLeadDTO> tousLesProspects = handler.findLeads(0, Double.MAX_VALUE, null);
            
            System.out.println("✓ Nombre total de prospects en base : " + tousLesProspects.size());
            System.out.println();

            // Étape 3 : Statistiques par région
            System.out.println("═══ ÉTAPE 3 : Répartition géographique ═══");
            compterParRegion(tousLesProspects);
            System.out.println();

            // Étape 4 : Statistiques financières
            System.out.println("═══ ÉTAPE 4 : Analyse des revenus ═══");
            analyserRevenus(tousLesProspects);
            System.out.println();

            // Étape 5 : Afficher quelques exemples
            System.out.println("═══ ÉTAPE 5 : Échantillon de prospects ═══");
            afficherEchantillon(tousLesProspects);
            System.out.println();

            // Étape 6 : Test de recherche par région
            System.out.println("═══ ÉTAPE 6 : Test recherche par région (Loire-Atlantique) ═══");
            List<InternalLeadDTO> loireAtlantique = handler.findLeads(0, Double.MAX_VALUE, "Loire-Atlantique");
            System.out.println("✓ Prospects en Loire-Atlantique : " + loireAtlantique.size());
            for (InternalLeadDTO p : loireAtlantique) {
                System.out.println("   - " + p.getFirstName() + " " + p.getLastName() + 
                    " (" + p.getCompanyName() + ") - " + formaterEuros(p.getAnnualRevenue()));
            }
            System.out.println();

            // Étape 7 : Test de recherche par revenus
            System.out.println("═══ ÉTAPE 7 : Test recherche par revenus (100k€ - 150k€) ═══");
            List<InternalLeadDTO> hautRevenus = handler.findLeads(100000, 150000, null);
            System.out.println("✓ Prospects avec revenus entre 100k€ et 150k€ : " + hautRevenus.size());
            for (InternalLeadDTO p : hautRevenus) {
                System.out.println("   - " + p.getFirstName() + " " + p.getLastName() + 
                    " - " + formaterEuros(p.getAnnualRevenue()) + " (" + p.getState() + ")");
            }
            System.out.println();

            // Résultat final
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            if (tousLesProspects.size() >= 48) {
                System.out.println("║                    ✓ VÉRIFICATION RÉUSSIE                    ║");
                System.out.println("║     Toutes les données ont été chargées correctement !       ║");
            } else {
                System.out.println("║                    ⚠ ATTENTION                               ║");
                System.out.println("║     Seulement " + tousLesProspects.size() + " prospects chargés (48 attendus)       ║");
            }
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("✗ ERREUR lors de la récupération des données :");
            e.printStackTrace();
        }
    }

    private static void compterParRegion(List<InternalLeadDTO> prospects) {
        int maineLoire = 0;
        int loireAtlantique = 0;
        int vendee = 0;
        int sarthe = 0;
        int mayenne = 0;
        int autres = 0;

        for (InternalLeadDTO p : prospects) {
            String region = p.getState();
            if (region == null) {
                autres++;
            } else if (region.equals("Maine-et-Loire")) {
                maineLoire++;
            } else if (region.equals("Loire-Atlantique")) {
                loireAtlantique++;
            } else if (region.equals("Vendée")) {
                vendee++;
            } else if (region.equals("Sarthe")) {
                sarthe++;
            } else if (region.equals("Mayenne")) {
                mayenne++;
            } else {
                autres++;
            }
        }

        System.out.println("   Maine-et-Loire     : " + maineLoire + " prospects");
        System.out.println("   Loire-Atlantique   : " + loireAtlantique + " prospects");
        System.out.println("   Vendée             : " + vendee + " prospects");
        System.out.println("   Sarthe             : " + sarthe + " prospects");
        System.out.println("   Mayenne            : " + mayenne + " prospects");
        if (autres > 0) {
            System.out.println("   Autres/Non défini  : " + autres + " prospects");
        }
    }

    private static void analyserRevenus(List<InternalLeadDTO> prospects) {
        if (prospects.isEmpty()) {
            System.out.println("   Aucun prospect à analyser");
            return;
        }

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0;

        for (InternalLeadDTO p : prospects) {
            double revenu = p.getAnnualRevenue();
            if (revenu < min) min = revenu;
            if (revenu > max) max = revenu;
            total += revenu;
        }

        double moyenne = total / prospects.size();

        System.out.println("   Revenu minimum     : " + formaterEuros(min));
        System.out.println("   Revenu maximum     : " + formaterEuros(max));
        System.out.println("   Revenu moyen       : " + formaterEuros(moyenne));
        System.out.println("   Revenu total       : " + formaterEuros(total));
    }

    private static void afficherEchantillon(List<InternalLeadDTO> prospects) {
        int nbAfficher = Math.min(5, prospects.size());
        
        System.out.println("   Premiers prospects :");
        for (int i = 0; i < nbAfficher; i++) {
            InternalLeadDTO p = prospects.get(i);
            System.out.println("   " + (i+1) + ". " + p.getFirstName() + " " + p.getLastName() + 
                " (" + p.getCompanyName() + ") - " + p.getCity() + ", " + p.getState());
        }

        if (prospects.size() > 5) {
            System.out.println("   ...");
            System.out.println("   Dernier prospect :");
            InternalLeadDTO dernier = prospects.get(prospects.size() - 1);
            System.out.println("   " + prospects.size() + ". " + dernier.getFirstName() + " " + 
                dernier.getLastName() + " (" + dernier.getCompanyName() + ") - " + 
                dernier.getCity() + ", " + dernier.getState());
        }
    }

    private static String formaterEuros(double montant) {
        return String.format("%,.0f €", montant);
    }
}
