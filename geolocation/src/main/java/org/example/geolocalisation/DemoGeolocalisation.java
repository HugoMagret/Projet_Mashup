package org.example.geolocalisation;

/**
 * Démonstration simple : passe une adresse complète en paramètre Gradle :
 *   ./gradlew :geolocation:executerGeoDemo -Padresse="2 boulevard de lavoisier, 49100, angers, france"
 */
public class DemoGeolocalisation {
    public static void main(String[] args) {
        String adresse = (args.length == 0)
                ? "2 boulevard de lavoisier, 49100, angers, france"
                : String.join(" ", args);
        ClientNominatim client = new ClientNominatim();
        client.geolocaliserAdresse(adresse)
                .ifPresentOrElse(
                        pt -> System.out.println("Adresse='" + adresse + "' => lat=" + pt.latitude() + " lon=" + pt.longitude() + " nom=" + pt.nomAffiche()),
                        () -> System.out.println("Aucun résultat pour: " + adresse)
                );
    }
}
