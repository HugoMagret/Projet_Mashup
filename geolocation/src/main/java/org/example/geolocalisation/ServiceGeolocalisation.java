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
 * SERVICE DE GEOLOCALISATION : transforme une adresse textuelle en coordonnees GPS
 * 
 * Utilise l'API REST Nominatim d'OpenStreetMap (service gratuit sans cle API).
 * 
 * EXEMPLE D'UTILISATION :
 *   ServiceGeolocalisation service = new ServiceGeolocalisation();
 *   Optional<PointGeographique> resultat = service.geolocaliserAdresse("2 boulevard lavoisier angers");
 *   if (resultat.isPresent()) {
 *       System.out.println("Latitude: " + resultat.get().latitude());
 *       System.out.println("Longitude: " + resultat.get().longitude());
 *   }
 * 
 * FONCTIONNEMENT :
 *   1. Encode l'adresse pour l'URL (remplace espaces par %20, etc.)
 *   2. Appelle https://nominatim.openstreetmap.org/search?format=json&limit=1&q=adresse
 *   3. Parse la reponse JSON
 *   4. Extrait latitude, longitude, nom complet
 *   5. Retourne Optional<PointGeographique> (vide si adresse introuvable)
 * 
 * MODE HORS LIGNE :
 *   Active avec GEO_OFFLINE=1 ou -Dgeo.offline=true
 *   Retourne des coordonnees simulees pour "lavoisier angers"
 */
public class ServiceGeolocalisation {

    private static final String URL_BASE = "https://nominatim.openstreetmap.org";
    
    // HttpClient = client HTTP moderne de Java 11+ (supporte HTTP/1.1 et HTTP/2)
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5)) // Timeout de connexion : 5 secondes max
        .build();
    
    // ObjectMapper = outil Jackson pour parser JSON
    private final ObjectMapper mapper = new ObjectMapper();
    
    // User-Agent = identification de l'application (OBLIGATOIRE pour Nominatim)
    // Format : "NomApp/Version (email.contact@exemple.com)"
    private final String userAgent;
    
    // Si true = pas d'internet, on simule avec des valeurs bidons
    private final boolean modeHorsLigne;

    /**
     * CONSTRUCTEUR 1 : on doit dire qui on est à Nominatim
     * @param userAgent exemple: "MonApp/1.0 (contact@monsite.com)"
     */
    public ServiceGeolocalisation(String userAgent) {
        this.userAgent = userAgent;
        // Active le mode simulation si on tape: 
        // GEO_OFFLINE=1 ./gradlew ... OU java -Dgeo.offline=true ...
        this.modeHorsLigne = Boolean.getBoolean("geo.offline") ||
                "1".equalsIgnoreCase(System.getenv("GEO_OFFLINE"));
    }

    /**
     * Constructeur par defaut avec User-Agent generique.
     * 
     * ATTENTION : L'email est fictif ! Dans un vrai projet, mettre un vrai contact.
     */
    public ServiceGeolocalisation() { 
        this("ProjetMashup-Etudiant/1.0 (etudiant-test@example.com)"); 
    }

    /**
     * MÉTHODE PRINCIPALE : transforme "10 rue machin Paris" en coordonnées GPS
     * @param adresseLibre exemple: "2 boulevard de lavoisier, angers, france"
     * @return coordonnées (lat/lon) + nom complet, ou rien si pas trouvé
     */
    public Optional<PointGeographique> geolocaliserAdresse(String adresseLibre) {
        // Si l'adresse est vide, pas la peine de chercher
        if (adresseLibre == null || adresseLibre.isBlank()) return Optional.empty();

        // MODE SIMULATION (quand pas d'internet ou pour tests)
        if (modeHorsLigne) {
            String minuscule = adresseLibre.toLowerCase();
            // On reconnaît seulement l'adresse de l'exemple du cours
            if (minuscule.contains("lavoisier") && minuscule.contains("angers")) {
                return Optional.of(new PointGeographique(
                        47.4795093,  // latitude de l'UFR sciences Angers
                        -0.6003698,  // longitude de l'UFR sciences Angers
                        "Boulevard de Lavoisier, Belle-Beille, Angers, Maine-et-Loire, Pays de la Loire, France"));
            }
            // Autre adresse en mode simulation = pas trouvé
            return Optional.empty();
        }

        // MODE NORMAL : on va vraiment sur internet
        try {
            // 1. Préparer l'adresse pour l'URL (remplacer espaces par %20, etc.)
            String adresseEncodee = URLEncoder.encode(adresseLibre, StandardCharsets.UTF_8);
            
            // 2. Construire l'URL complète
            String url = URL_BASE + "/search?format=json&limit=1&q=" + adresseEncodee;
            // Explication de l'URL :
            // ?format=json = je veux du JSON (pas du XML)
            // &limit=1 = donne-moi seulement le meilleur résultat
            // &q=... = l'adresse à chercher

            // 3. Préparer la requête HTTP
            HttpRequest requete = HttpRequest.newBuilder()
                    .GET()                              // méthode GET (lire des données)
                    .uri(URI.create(url))               // où aller
                    .timeout(Duration.ofSeconds(30))    // 30 secondes max pour la réponse
                    .header("User-Agent", userAgent)    // qui on est (obligatoire)
                    .build();                           // créer l'objet requête

            // 4. Envoyer la requête et récupérer la réponse
            HttpResponse<String> reponse = httpClient.send(requete, HttpResponse.BodyHandlers.ofString());

            // 5. Vérifier que ça a marché (code 200 = OK)
            if (reponse.statusCode() != 200) {
                throw new GeolocalisationException("Nominatim a répondu avec erreur: " + reponse.statusCode());
            }

            // 6. Lire le JSON de la réponse
            JsonNode resultatJson = mapper.readTree(reponse.body());
            // Nominatim renvoie un tableau [...] même s'il n'y a qu'un résultat
            if (!resultatJson.isArray() || resultatJson.isEmpty()) {
                return Optional.empty(); // tableau vide = pas trouvé
            }

            // 7. Prendre le premier (et seul) résultat
            JsonNode premierResultat = resultatJson.get(0);
            double latitude = premierResultat.path("lat").asDouble();
            double longitude = premierResultat.path("lon").asDouble();
            String nomComplet = premierResultat.path("display_name").asText();

            return Optional.of(new PointGeographique(latitude, longitude, nomComplet));
            
        } catch (InterruptedException ie) {
            // Le thread a été interrompu pendant l'attente
            Thread.currentThread().interrupt();
            throw new GeolocalisationException("Opération interrompue", ie);
        } catch (IOException ioe) {
            // Problème réseau (pas d'internet, timeout, etc.)
            // Au lieu de planter, on dit "pas trouvé"
            return Optional.empty();
        }
    }
}
