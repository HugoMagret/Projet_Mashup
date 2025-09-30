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
 * Objectif pédagogique : montrer comment construire une requête HTTP, gérer un timeout,
 * parser la réponse JSON et retourner un objet métier simple.
 *
 * Rappels :
 *  - Nominatim ne demande pas d'API key (mais exige un User-Agent explicite)
 *  - On se limite volontairement au PREMIER résultat (limit=1)
 *  - On extrait UNIQUEMENT latitude, longitude et display_name
 *  - On fournit un MODE HORS-LIGNE (simulation) activable via -Dgeo.offline=true ou env GEO_OFFLINE=1
 */
public class ClientNominatim {

    private static final String URL_BASE = "https://nominatim.openstreetmap.org";
    // Client HTTP réutilisable (réduction du coût de création de sockets)
    private final HttpClient httpClient = HttpClient.newBuilder()
        // Timeout de connexion (établissement de la connexion TCP)
        .connectTimeout(Duration.ofSeconds(5))
            .build();
    // ObjectMapper Jackson pour lire / naviguer dans le JSON
    private final ObjectMapper mapper = new ObjectMapper();
    // User-Agent envoyé à Nominatim (doit contenir une info de contact selon leur politique d'usage)
    private final String userAgent;
    // Si true : on ne fait PAS d'appel réseau, on renvoie des données simulées
    private final boolean modeHorsLigne;

    /**
     * @param userAgent valeur User-Agent (doit contenir un contact conformément aux règles Nominatim)
     */
    public ClientNominatim(String userAgent) {
        this.userAgent = userAgent; // on stocke la valeur reçue
        // Activation du mode simulation si :
        //  - l'option JVM -Dgeo.offline=true est présente
        //  - OU la variable d'environnement GEO_OFFLINE vaut "1"
        this.modeHorsLigne = Boolean.getBoolean("geo.offline") ||
                "1".equalsIgnoreCase(System.getenv("GEO_OFFLINE"));
    }

    // Constructeur pratique : fournit un User-Agent par défaut (à personnaliser dans un vrai projet)
    public ClientNominatim() { this("ProjetMashup/1.0 (contact@example.com)"); }

    /**
     * Géolocalise une adresse libre : retourne le premier résultat si trouvé.
     * @param adresseLibre ex: "2 boulevard de lavoisier, 49100, angers, france"
     */
    public Optional<PointGeographique> geolocaliserAdresse(String adresseLibre) {
        // Validation basique : adresse null ou vide => aucun résultat
        if (adresseLibre == null || adresseLibre.isBlank()) return Optional.empty();

        // 1. MODE HORS-LIGNE : on évite le réseau (tests, situation sans internet)
        //    On reconnaît l'adresse de l'énoncé via quelques mots-clés.
        if (modeHorsLigne) {
            String normalise = adresseLibre.toLowerCase(); // pour comparaison insensible à la casse
            if (normalise.contains("lavoisier") && normalise.contains("angers")) {
                return Optional.of(new PointGeographique(
                        47.4795093,
                        -0.6003698,
                        "Boulevard de Lavoisier, Belle-Beille, Angers, Maine-et-Loire, Pays de la Loire, France"));
            }
            // Aucune autre adresse simulée => on retourne vide
            return Optional.empty();
        }

        // 2. MODE NORMAL (réseau)
        try {
            // Encodage de l'adresse pour la mettre dans l'URL (espaces => %20, etc.)
            String q = URLEncoder.encode(adresseLibre, StandardCharsets.UTF_8);
            // Construction de l'URL avec format JSON + 1 seul résultat
            String url = URL_BASE + "/search?format=json&limit=1&q=" + q;

            // Construction de la requête HTTP
            HttpRequest requete = HttpRequest.newBuilder()
                    .GET() // méthode HTTP
                    .uri(URI.create(url)) // cible à appeler
                    .timeout(Duration.ofSeconds(30)) // durée max totale pour la requête
                    .header("User-Agent", userAgent) // obligation Nominatim
                    .build(); // création de l'objet immutable

            // Envoi synchrone de la requête et récupération de la réponse texte
            HttpResponse<String> reponse = httpClient.send(requete, HttpResponse.BodyHandlers.ofString());

            // Vérification HTTP 200 OK
            if (reponse.statusCode() != 200) {
                throw new GeolocalisationException("Appel Nominatim échoué statut=" + reponse.statusCode());
            }

            // Parsing du JSON (racine = tableau de résultats)
            JsonNode racine = mapper.readTree(reponse.body());
            if (!racine.isArray() || racine.isEmpty()) return Optional.empty();

            // Premier objet = meilleure correspondance
            JsonNode premier = racine.get(0);
            double lat = premier.path("lat").asDouble(); // path() évite NullPointer
            double lon = premier.path("lon").asDouble();
            String display = premier.path("display_name").asText();

            return Optional.of(new PointGeographique(lat, lon, display));
        } catch (InterruptedException ie) {
            // Thread interrompu : on restaure le flag d'interruption et on remonte une exception métier
            Thread.currentThread().interrupt();
            throw new GeolocalisationException("Requête interrompue", ie);
        } catch (IOException ioe) {
            // Erreur réseau / timeout lecture : on CHOISIT de retourner vide (stratégie tolérante)
            return Optional.empty();
        }
    }
}
