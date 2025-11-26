package org.example.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Générateur de données initiales pour InternalCRM.
 * 
 * Crée 48 prospects fictifs pour tester le système.
 * Utilisé au démarrage du serveur pour avoir une base de données de démonstration.
 */
public class InitialDataLoader {

    /**
     * Génère la liste complète des prospects initiaux.
     * 
     * @return Liste de 48 prospects avec données variées (revenus, régions, dates)
     */
    public static List<InternalLeadDTO> genererProspectsInitiaux() {
        List<InternalLeadDTO> prospects = new ArrayList<>();

        // Région : Maine-et-Loire (49)
        prospects.add(creerProspect("Jean", "Dupont", "Acme Corp", 50000, "+33241123456", 
            "1 rue Exemple", "49100", "Angers", "Maine-et-Loire", "France", "2024-01-15T09:00:00Z"));
        prospects.add(creerProspect("Sophie", "Martin", "Tech Innovations", 75000, "+33241234567",
            "5 avenue de la Gare", "49000", "Angers", "Maine-et-Loire", "France", "2024-02-10T10:30:00Z"));
        prospects.add(creerProspect("Pierre", "Durand", "Green Energy", 120000, "+33241345678",
            "12 boulevard Foch", "49100", "Angers", "Maine-et-Loire", "France", "2024-03-05T14:15:00Z"));
        prospects.add(creerProspect("Marie", "Leroy", "Consulting Plus", 95000, "+33241456789",
            "8 rue Voltaire", "49000", "Angers", "Maine-et-Loire", "France", "2024-03-20T11:00:00Z"));
        prospects.add(creerProspect("Luc", "Bernard", "Digital Solutions", 110000, "+33241567890",
            "23 rue Jean Jaurès", "49100", "Angers", "Maine-et-Loire", "France", "2024-04-12T08:45:00Z"));
        prospects.add(creerProspect("Claire", "Petit", "Innovation Lab", 85000, "+33241678901",
            "15 place du Ralliement", "49000", "Angers", "Maine-et-Loire", "France", "2024-05-18T16:20:00Z"));
        prospects.add(creerProspect("Thomas", "Robert", "Startup Factory", 65000, "+33241789012",
            "9 rue des Lices", "49100", "Angers", "Maine-et-Loire", "France", "2024-06-22T13:30:00Z"));
        prospects.add(creerProspect("Emma", "Richard", "Tech Hub", 145000, "+33241890123",
            "31 rue Boisnet", "49000", "Angers", "Maine-et-Loire", "France", "2024-07-08T09:15:00Z"));
        prospects.add(creerProspect("Lucas", "Moreau", "Data Analytics", 98000, "+33241901234",
            "7 rue Beaurepaire", "49100", "Angers", "Maine-et-Loire", "France", "2024-08-14T10:50:00Z"));
        prospects.add(creerProspect("Léa", "Simon", "Cloud Services", 72000, "+33241012345",
            "18 rue Saint-Aubin", "49000", "Angers", "Maine-et-Loire", "France", "2024-09-03T15:40:00Z"));

        // Région : Loire-Atlantique (44)
        prospects.add(creerProspect("Alice", "Laurent", "Atlantic Tech", 135000, "+33240111222",
            "10 quai de la Fosse", "44000", "Nantes", "Loire-Atlantique", "France", "2024-01-20T10:00:00Z"));
        prospects.add(creerProspect("Hugo", "Girard", "Ocean Industries", 88000, "+33240222333",
            "25 rue Crébillon", "44000", "Nantes", "Loire-Atlantique", "France", "2024-02-15T11:30:00Z"));
        prospects.add(creerProspect("Chloé", "Roux", "Maritime Services", 105000, "+33240333444",
            "5 place Royale", "44000", "Nantes", "Loire-Atlantique", "France", "2024-03-10T14:00:00Z"));
        prospects.add(creerProspect("Antoine", "Vincent", "West Consulting", 92000, "+33240444555",
            "14 rue de Strasbourg", "44000", "Nantes", "Loire-Atlantique", "France", "2024-04-05T09:45:00Z"));
        prospects.add(creerProspect("Manon", "Fournier", "Innovation Ouest", 125000, "+33240555666",
            "8 cours Cambronne", "44100", "Nantes", "Loire-Atlantique", "France", "2024-05-12T16:15:00Z"));
        prospects.add(creerProspect("Julien", "Morel", "Digital West", 78000, "+33240666777",
            "12 rue Kervégan", "44000", "Nantes", "Loire-Atlantique", "France", "2024-06-18T13:20:00Z"));
        prospects.add(creerProspect("Sarah", "Lefebvre", "Atlantic Data", 150000, "+33240777888",
            "20 allée Duquesne", "44000", "Nantes", "Loire-Atlantique", "France", "2024-07-25T10:10:00Z"));
        prospects.add(creerProspect("Maxime", "Lefevre", "Tech Nantes", 67000, "+33240888999",
            "3 rue Scribe", "44000", "Nantes", "Loire-Atlantique", "France", "2024-08-30T15:50:00Z"));
        prospects.add(creerProspect("Camille", "Andre", "West Solutions", 115000, "+33240999000",
            "17 rue de l'Héronnière", "44000", "Nantes", "Loire-Atlantique", "France", "2024-09-10T11:25:00Z"));
        prospects.add(creerProspect("Nathan", "Michel", "Oceanic Tech", 82000, "+33240000111",
            "9 place Graslin", "44000", "Nantes", "Loire-Atlantique", "France", "2024-10-01T14:35:00Z"));

        // Région : Vendée (85)
        prospects.add(creerProspect("Julie", "Garcia", "Vendée Innovation", 95000, "+33251111222",
            "15 rue Clemenceau", "85000", "La Roche-sur-Yon", "Vendée", "France", "2024-01-25T09:30:00Z"));
        prospects.add(creerProspect("Alexandre", "David", "Coastal Services", 73000, "+33251222333",
            "8 boulevard Aristide Briand", "85000", "La Roche-sur-Yon", "Vendée", "France", "2024-02-20T11:00:00Z"));
        prospects.add(creerProspect("Laura", "Thomas", "Sud Tech", 108000, "+33251333444",
            "22 rue du Maréchal Foch", "85100", "Les Sables-d'Olonne", "Vendée", "France", "2024-03-15T15:30:00Z"));
        prospects.add(creerProspect("Arthur", "Bertrand", "Atlantic Business", 85000, "+33251444555",
            "5 place Napoléon", "85000", "La Roche-sur-Yon", "Vendée", "France", "2024-04-10T10:20:00Z"));
        prospects.add(creerProspect("Inès", "Martinez", "Vendée Solutions", 118000, "+33251555666",
            "11 rue Hoche", "85000", "La Roche-sur-Yon", "Vendée", "France", "2024-05-22T13:45:00Z"));
        prospects.add(creerProspect("Louis", "Rousseau", "Coast Industries", 76000, "+33251666777",
            "19 promenade Clemenceau", "85100", "Les Sables-d'Olonne", "Vendée", "France", "2024-06-28T09:10:00Z"));
        prospects.add(creerProspect("Zoé", "Blanc", "Innovation 85", 142000, "+33251777888",
            "7 rue Travot", "85000", "La Roche-sur-Yon", "Vendée", "France", "2024-07-15T16:00:00Z"));
        prospects.add(creerProspect("Gabriel", "Lopez", "Sud Consulting", 69000, "+33251888999",
            "13 rue des Halles", "85000", "La Roche-sur-Yon", "Vendée", "France", "2024-08-20T12:30:00Z"));
        prospects.add(creerProspect("Jade", "Sanchez", "Vendée Digital", 101000, "+33251999000",
            "6 place de la Liberté", "85100", "Les Sables-d'Olonne", "Vendée", "France", "2024-09-12T14:15:00Z"));
        prospects.add(creerProspect("Tom", "Guerin", "Tech Vendée", 87000, "+33251000111",
            "24 rue Joffre", "85000", "La Roche-sur-Yon", "Vendée", "France", "2024-10-05T10:40:00Z"));

        // Région : Sarthe (72)
        prospects.add(creerProspect("Élise", "Faure", "Le Mans Tech", 91000, "+33243111222",
            "12 place de la République", "72000", "Le Mans", "Sarthe", "France", "2024-02-01T09:00:00Z"));
        prospects.add(creerProspect("Raphaël", "Garnier", "Automotive Solutions", 130000, "+33243222333",
            "8 avenue Bollée", "72000", "Le Mans", "Sarthe", "France", "2024-03-12T11:20:00Z"));
        prospects.add(creerProspect("Anaïs", "Chevalier", "Innovation Sarthe", 79000, "+33243333444",
            "15 rue de la Mariette", "72000", "Le Mans", "Sarthe", "France", "2024-04-18T14:50:00Z"));
        prospects.add(creerProspect("Enzo", "Renard", "Circuit Industries", 97000, "+33243444555",
            "5 boulevard René Levasseur", "72000", "Le Mans", "Sarthe", "France", "2024-05-25T10:30:00Z"));
        prospects.add(creerProspect("Lola", "Dupuis", "Tech72", 112000, "+33243555666",
            "20 rue Gambetta", "72000", "Le Mans", "Sarthe", "France", "2024-06-30T15:10:00Z"));
        prospects.add(creerProspect("Hugo", "Roy", "Sarthe Consulting", 71000, "+33243666777",
            "9 place des Jacobins", "72000", "Le Mans", "Sarthe", "France", "2024-07-10T09:40:00Z"));
        prospects.add(creerProspect("Mathilde", "Clement", "Motor Tech", 138000, "+33243777888",
            "17 avenue du Général de Gaulle", "72000", "Le Mans", "Sarthe", "France", "2024-08-15T13:55:00Z"));
        prospects.add(creerProspect("Paul", "Gauthier", "Le Mans Digital", 64000, "+33243888999",
            "11 rue Wilbur Wright", "72000", "Le Mans", "Sarthe", "France", "2024-09-20T11:15:00Z"));
        prospects.add(creerProspect("Léna", "Lambert", "Innovation 24h", 104000, "+33243999000",
            "6 place de l'Eperon", "72000", "Le Mans", "Sarthe", "France", "2024-10-10T16:25:00Z"));
        prospects.add(creerProspect("Adam", "Fontaine", "Sarthe Solutions", 89000, "+33243000111",
            "14 rue de Bolton", "72000", "Le Mans", "Sarthe", "France", "2024-10-25T12:50:00Z"));

        // Région : Mayenne (53)
        prospects.add(creerProspect("Amélie", "Bonnet", "Mayenne Tech", 68000, "+33243121314",
            "18 rue de la Paix", "53000", "Laval", "Mayenne", "France", "2024-03-01T10:15:00Z"));
        prospects.add(creerProspect("Théo", "Dupont", "Nord Solutions", 93000, "+33243151617",
            "7 place du 11 novembre", "53000", "Laval", "Mayenne", "France", "2024-04-22T14:30:00Z"));
        prospects.add(creerProspect("Céline", "Mercier", "Innovation 53", 81000, "+33243181920",
            "12 rue de Bretagne", "53000", "Laval", "Mayenne", "France", "2024-05-30T09:20:00Z"));
        prospects.add(creerProspect("Florian", "Giraud", "Mayenne Industries", 106000, "+33243212223",
            "9 avenue Robert Buron", "53000", "Laval", "Mayenne", "France", "2024-06-15T16:45:00Z"));
        prospects.add(creerProspect("Océane", "Perrin", "Tech Laval", 75000, "+33243242526",
            "16 boulevard de la Communauté", "53000", "Laval", "Mayenne", "France", "2024-07-20T11:30:00Z"));
        prospects.add(creerProspect("Ethan", "Morin", "Digital Mayenne", 122000, "+33243272829",
            "5 rue Mazagran", "53000", "Laval", "Mayenne", "France", "2024-08-25T15:00:00Z"));
        prospects.add(creerProspect("Mathis", "Blanchard", "Nord Consulting", 66000, "+33243303132",
            "21 rue de Rennes", "53000", "Laval", "Mayenne", "France", "2024-09-18T10:05:00Z"));
        prospects.add(creerProspect("Nina", "Riviere", "Mayenne Data", 99000, "+33243333435",
            "10 place Hardy de Levaré", "53000", "Laval", "Mayenne", "France", "2024-10-12T13:40:00Z"));
        prospects.add(creerProspect("Ronan", "Caca", "Mayenne Data", 99000, "+33243333435",
                "10 place Hardy de Levaré", "53000", "Laval", "Mayenne", "France", "2024-10-12T13:40:00Z"));

        return prospects;
    }

    /**
     * Crée un prospect avec tous les champs renseignés.
     * Méthode utilitaire pour éviter la répétition de code.
     */
    private static InternalLeadDTO creerProspect(
            String prenom, String nom, String entreprise, double revenus, String telephone,
            String rue, String codePostal, String ville, String region, String pays, String dateCreation) {
        
        InternalLeadDTO prospect = new InternalLeadDTO();
        prospect.setFirstName(prenom);
        prospect.setLastName(nom);
        prospect.setCompanyName(entreprise);
        prospect.setAnnualRevenue(revenus);
        prospect.setPhone(telephone);
        prospect.setStreet(rue);
        prospect.setPostalCode(codePostal);
        prospect.setCity(ville);
        prospect.setState(region);
        prospect.setCountry(pays);
        prospect.setCreationDate(dateCreation);
        
        return prospect;
    }
}
