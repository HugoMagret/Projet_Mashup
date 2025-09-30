package org.example.geolocalisation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * Client minimal de l'API publique Nominatim (OpenStreetMap) pour la section 2.4.
 * OBJECTIF : envoyer une requête HTTP GET contenant une adresse et récupérer
 * uniquement la latitude, la longitude et le nom affiché.
 *
 * POINTS IMPORTANTS :
 *  - Pas de clé API nécessaire pour Nominatim, mais il faut un User-Agent explicite.
 *  - On limite volontairement la réponse à 1 résultat (paramètre limit=1) pour rester simple.
 *  - On ne lit qu'un petit sous-ensemble du JSON retourné.
 *  - On encapsule le résultat dans Optional<PointGeographique> (vide si rien trouvé).
 */
public class ClientNominatim {

    private static final String URL_BASE = "https://nominatim.openstreetmap.org";
    // Client HTTP standard (Java 11) réutilisable pour plusieurs appels.
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    // Mapper JSON (Jackson) pour lire la chaîne JSON en arbre (JsonNode).
    private final ObjectMapper mapper = new ObjectMapper();
    // Chaîne User-Agent envoyée à chaque requête (exigé par Nominatim).
    private final String userAgent;

    /**
     * @param userAgent valeur User-Agent (doit contenir un contact conformément aux règles Nominatim)
     */
    public ClientNominatim(String userAgent) {
        // Constructeur principal : on garde la valeur fournie.
        this.userAgent = userAgent;
    }

    public ClientNominatim() {
        // Surcharge pratique : si l'utilisateur ne passe rien, on utilise une valeur défaut.
        // Cette valeur doit idéalement contenir un moyen de contact (email/projet) pour respecter la politique OSM.
        this("ProjetMashup/1.0 (contact@example.com)");
    }

    /**
     * Géolocalise une adresse libre : retourne le premier résultat si trouvé.
     * @param adresseLibre ex: "2 boulevard de lavoisier, 49100, angers, france"
     */
    public Optional<PointGeographique> geolocaliserAdresse(String adresseLibre) {
        // 1. Validation basique : si chaîne vide ou null -> aucun résultat.
        if (adresseLibre == null || adresseLibre.isBlank()) return Optional.empty();
        try {
            // 2. Encodage URL de l'adresse (espaces, accents...) en UTF-8 pour la requête HTTP.
            String adresseEncodee = URLEncoder.encode(adresseLibre, StandardCharsets.UTF_8);

            // 3. Construction de l'URL finale avec paramètres :
            //    format=json  -> format de sortie JSON
            //    limit=1      -> nous ne voulons qu'un seul résultat
            //    q=...        -> la requête (adresse encodée)
            String url = URL_BASE + "/search?format=json&limit=1&q=" + adresseEncodee;

            // 4. Construction de la requête HTTP GET (timeout de 10s + User-Agent requis par Nominatim).
            HttpRequest requete = HttpRequest.newBuilder()
                    .GET()                                    // Méthode HTTP GET
                    .uri(URI.create(url))                     // URL cible
                    .timeout(Duration.ofSeconds(10))          // Timeout total de l'appel
                    .header("User-Agent", userAgent)         // Identification du client
                    .build();                                 // Création immuable de la requête

            // 5. Envoi de la requête et récupération de la réponse sous forme de chaîne.
            HttpResponse<String> reponse = httpClient.send(requete, HttpResponse.BodyHandlers.ofString());

            // 6. Vérification du code HTTP. 200 = OK. Sinon on lève une exception métier.
            if (reponse.statusCode() != 200) {
                throw new GeolocalisationException("Appel Nominatim échoué statut=" + reponse.statusCode());
            }

            // 7. Parsing du corps JSON -> arbre JsonNode.
            JsonNode racine = mapper.readTree(reponse.body());

            // 8. Si le JSON n'est pas un tableau ou est vide -> aucun résultat.
            if (!racine.isArray() || racine.isEmpty()) return Optional.empty();

            // 9. On prend le premier élément (puisque limit=1 on s'attend à 0 ou 1).
            JsonNode premier = racine.get(0);

            // 10. Extraction des champs lat / lon / display_name.
            double latitude = premier.path("lat").asDouble();
            double longitude = premier.path("lon").asDouble();
            String nomAffiche = premier.path("display_name").asText();

            // 11. On construit et retourne l'objet métier enveloppé dans Optional.
            return Optional.of(new PointGeographique(latitude, longitude, nomAffiche));
        } catch (IOException | InterruptedException e) {
            // 12. En cas d'interruption (InterruptedException) on réactive le flag d'interruption du thread.
            Thread.currentThread().interrupt();
            // 13. On transforme l'erreur technique en exception métier plus lisible.
            throw new GeolocalisationException("Erreur lors de l'appel Nominatim", e);
        }
    }
}
