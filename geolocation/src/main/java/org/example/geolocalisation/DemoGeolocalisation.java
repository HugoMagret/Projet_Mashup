package org.example.geolocalisation;

/**
 * DÉMONSTRATION : utilisation du service de géolocalisation
 * 
 * COMMANDE :
 *   ./gradlew :geolocation:executerGeoDemo -Padresse="10 rue de la paix paris"
 * 
 * FONCTIONNEMENT :
 *   1. Prend l'adresse fournie en argument (ou adresse par défaut)
 *   2. Appelle l'API Nominatim sur internet pour obtenir lat/lon
 *   3. Affiche le résultat avec coordonnées GPS et nom complet
 * 
 * EXEMPLE DE SORTIE :
 *   Adresse='2 boulevard de lavoisier, angers' 
 *     => lat=47.4738 lon=-0.5969 
 *        nom=Boulevard de Lavoisier, Angers, Maine-et-Loire, France
 */
public class DemoGeolocalisation {
    public static void main(String[] args) {
        // Si aucun argument, utiliser l'adresse de l'exemple du cours
        String adresse = (args.length == 0)
                ? "2 boulevard de lavoisier, 49100, angers, france"
                // Sinon, concaténer tous les arguments en une seule chaîne
                : String.join(" ", args);

        // Créer le client qui va interroger l'API Nominatim
        ServiceGeolocalisation client = new ServiceGeolocalisation();

        // Demander la géolocalisation et afficher le résultat
        // Optional = conteneur Java pour valeur potentiellement absente (évite null)
        client.geolocaliserAdresse(adresse)
                .ifPresentOrElse(
                        // Si trouvé : afficher latitude, longitude, nom complet
                        point -> System.out.println("Adresse='" + adresse + "' => lat=" + point.latitude() + " lon=" + point.longitude() + " nom=" + point.nomAffiche()),
                        // Si pas trouvé : message d'erreur
                        () -> System.out.println("Aucun résultat pour: " + adresse)
                );
    }
}
