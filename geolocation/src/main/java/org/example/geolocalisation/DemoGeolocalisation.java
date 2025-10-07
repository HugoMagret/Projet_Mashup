package org.example.geolocalisation;

/**
 * DÉMO : comment utiliser la géolocalisation
 * 
 * COMMANDE :
 *   ./gradlew :geolocation:executerGeoDemo -Padresse="10 rue de la paix paris"
 * 
 * QUE FAIT CE PROGRAMME :
 *   1. Il prend l'adresse qu'on lui donne (ou celle par défaut)
 *   2. Il appelle Nominatim sur internet pour avoir lat/lon
 *   3. Il affiche le résultat
 */
public class DemoGeolocalisation {
    public static void main(String[] args) {
        // Si pas d'argument, on prend l'adresse de l'exemple du cours
        String adresse = (args.length == 0)
                ? "2 boulevard de lavoisier, 49100, angers, france"
                // Sinon on colle tous les morceaux d'arguments
                : String.join(" ", args);

        // Créer le client qui va sur internet
        ClientNominatim client = new ClientNominatim();

        // Demander la géolocalisation et afficher le résultat
        client.geolocaliserAdresse(adresse)
                .ifPresentOrElse(
                        // Si trouvé : afficher lat, lon, nom complet
                        point -> System.out.println("Adresse='" + adresse + "' => lat=" + point.latitude() + " lon=" + point.longitude() + " nom=" + point.nomAffiche()),
                        // Si pas trouvé : message d'erreur
                        () -> System.out.println("Aucun résultat pour: " + adresse)
                );
    }
}
