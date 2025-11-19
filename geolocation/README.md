# Module de Géolocalisation

## Description

Convertit une adresse textuelle en coordonnées GPS (latitude, longitude) via l'API Nominatim d'OpenStreetMap.

**Exigence énoncé** : Section 2.1 - Service de géolocalisation REST

## Architecture

```
geolocation/
├── src/main/java/org/example/geolocalisation/
│   ├── ServiceGeolocalisation.java     # Client HTTP Nominatim (API REST)
│   ├── PointGeographique.java          # Record (lat, lon, nom)
│   ├── GeolocalisationException.java   # Exception personnalisée
│   └── DemoGeolocalisation.java        # Démo CLI
└── build.gradle
```

## Utilisation dans VirtualCRM

VirtualCRM utilise ce module comme **librairie Java** (pas de réseau) :

```java
// Dans VirtualCRM
import org.example.geolocalisation.ServiceGeolocalisation;

ServiceGeolocalisation geo = new ServiceGeolocalisation();
Optional<PointGeographique> point = geo.geolocaliserAdresse("10 rue Paris");
point.ifPresent(p -> System.out.println("Lat=" + p.latitude() + " Lon=" + p.longitude()));
```

**Configuration Gradle VirtualCRM** :
```gradle
dependencies {
    implementation project(':geolocation')  // Dépendance directe
}
```

## API Nominatim

- **URL** : `https://nominatim.openstreetmap.org/search`
- **Méthode** : GET
- **Paramètres** : `?format=json&limit=1&q=adresse`
- **User-Agent obligatoire** : "VotreApp/1.0 (votre.email@etud.univ-angers.fr)"
- **Parsing** : Jackson (`com.fasterxml.jackson.core:jackson-databind:2.17.2`)

**Exemple requête** :
```
GET https://nominatim.openstreetmap.org/search?format=json&limit=1&q=tour%20eiffel%20paris
Réponse : [{"lat":"48.8583","lon":"2.2945","display_name":"Tour Eiffel, Paris..."}]
```

## Gestion des erreurs

- Adresse vide → `Optional.empty()`
- Adresse introuvable → `Optional.empty()`
- Timeout réseau (30s) → `Optional.empty()`
- Erreur HTTP → `Optional.empty()`
- Erreur technique grave → `GeolocalisationException`

## Mode hors ligne

Pour tests sans internet :

```bash
GEO_OFFLINE=1 ./gradlew :geolocation:executerGeoDemo -Padresse="lavoisier angers"
```

Retourne coordonnées simulées pour "lavoisier angers" uniquement.

## Tests

```bash
# Compiler
./gradlew :geolocation:build

# Tester avec adresse
./gradlew :geolocation:executerGeoDemo -Padresse="tour eiffel paris"

# Adresse par défaut (Lavoisier Angers)
./gradlew :geolocation:executerGeoDemo
```

**Résultat attendu** :
```
Adresse='tour eiffel paris' 
  => lat=48.8583 lon=2.2945 
     nom=Tour Eiffel, Avenue Anatole France, Paris, Île-de-France, France
```

## Où trouver chaque exigence (Section 2.1)

| Exigence | Fichier | Lignes |
|----------|---------|--------|
| 2.1.1 Appel REST Nominatim | `ServiceGeolocalisation.java` | 95-105 |
| 2.1.2 Parsing JSON | `ServiceGeolocalisation.java` | 119-125 |
| 2.1.3 Gestion erreurs | `ServiceGeolocalisation.java` | 62, 109, 130 |
| 2.1.4 Mode offline | `ServiceGeolocalisation.java` | 40-42, 74-82 |

