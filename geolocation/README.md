# 🌍 Module de Géolocalisation

## 📋 Description

Ce module transforme des adresses écrites en texte libre (ex: "10 rue de la Paix Paris") en coordonnées GPS précises (latitude, longitude). Il utilise le service **Nominatim** d'OpenStreetMap, qui est gratuit et ne nécessite pas d'API key.

### ✨ Fonctionnalités

- **Géocodage d'adresses** : Convertit une adresse textuelle en coordonnées GPS
- **Service gratuit** : Utilise Nominatim (OpenStreetMap) sans limitation ni coût
- **Mode hors ligne** : Simulation pour les tests sans connexion internet
- **Gestion d'erreurs** : Timeouts, erreurs réseau, adresses introuvables
- **Format français** : Noms de variables et classes en français

### 🎯 Exemple concret

```
Entrée  : "2 boulevard de lavoisier, 49100, angers, france"
Sortie  : lat=47.4738, lon=-0.5969, nom="Boulevard de Lavoisier, Angers, Maine-et-Loire, ..."
```

## 🏗️ Architecture

```
geolocation/
├── src/main/java/org/example/geolocalisation/
│   ├── ClientNominatim.java        # Client HTTP pour Nominatim
│   ├── PointGeographique.java      # Résultat (lat, lon, nom)
│   ├── GeolocalisationException.java # Exception personnalisée
│   └── DemoGeolocalisation.java    # Application de démonstration
├── build.gradle                    # Configuration du build
└── README.md                       # Ce fichier
```

### 📦 Classes principales

| Classe | Rôle | Utilisation |
|--------|------|-------------|
| `ClientNominatim` | Client HTTP vers l'API Nominatim | `client.geolocaliserAdresse("adresse")` |
| `PointGeographique` | Structure de données (record) | `point.latitude()`, `point.longitude()` |
| `GeolocalisationException` | Gestion des erreurs | Levée automatique en cas de problème |
| `DemoGeolocalisation` | Démo en ligne de commande | Application d'exemple |

## 🚀 Compilation et Exécution

### Prérequis
- **Java 17+** (pour les records et HttpClient)
- **Gradle** (inclus via wrapper)
- **Connexion internet** (pour Nominatim, sauf en mode offline)

### 🔨 Compilation

```bash
# Depuis la racine du projet
./gradlew :geolocation:build

# Ou depuis le dossier geolocation/
../gradlew build
```

### ⚡ Exécution de la démo

#### Avec une adresse personnalisée :
```bash
./gradlew :geolocation:executerGeoDemo -Padresse="10 rue de la paix paris"
```

#### Avec l'adresse par défaut (du cours) :
```bash
./gradlew :geolocation:executerGeoDemo
```

#### Exemples d'adresses testées :
```bash
# Angers (adresse du cours)
./gradlew :geolocation:executerGeoDemo -Padresse="2 boulevard de lavoisier, 49100, angers, france"

# Paris
./gradlew :geolocation:executerGeoDemo -Padresse="tour eiffel paris"

# Nantes
./gradlew :geolocation:executerGeoDemo -Padresse="place du commerce nantes"
```

## 🧪 Tests et Mode Hors Ligne

### Mode simulation (sans internet)

Pour tester sans connexion ou pendant le développement :

```bash
# Via variable d'environnement
GEO_OFFLINE=1 ./gradlew :geolocation:executerGeoDemo

# Via propriété système
./gradlew :geolocation:executerGeoDemo -Dgeo.offline=true
```

En mode hors ligne, seule l'adresse de test du cours est reconnue et retourne des coordonnées simulées.

### 📊 Résultats attendus

#### ✅ Succès :
```
Adresse='2 boulevard de lavoisier, 49100, angers, france' => lat=47.4738 lon=-0.5969 nom=Boulevard de Lavoisier, Angers, Maine-et-Loire, Pays de la Loire, France métropolitaine, 49100, France
```

#### ❌ Adresse introuvable :
```
Aucun résultat pour: adresse inexistante xyz
```

#### ⚠️ Erreur réseau :
```
Erreur de géolocalisation: Connection timed out
```

## 🔧 Configuration et Personalisation

### Timeout et User-Agent

Dans `ClientNominatim.java` :

```java
// Timeout de connexion (par défaut : 5 secondes)
.connectTimeout(Duration.ofSeconds(5))

// Timeout de requête (par défaut : 30 secondes)
.timeout(Duration.ofSeconds(30))

// User-Agent pour Nominatim (obligatoire)
"ProjetMashup-Etudiant/1.0 (etudiant-test@example.com)"
```

### Modifier l'adresse par défaut

Dans `DemoGeolocalisation.java`, ligne 16 :

```java
String adresse = (args.length == 0)
    ? "VOTRE_ADRESSE_PAR_DEFAUT"
    : String.join(" ", args);
```

## 🌐 API Nominatim

### URL de base
`https://nominatim.openstreetmap.org/search`

### Paramètres utilisés
- `format=json` : Réponse en JSON
- `limit=1` : Un seul résultat (le meilleur)
- `q=adresse` : L'adresse à géocoder (URL-encodée)

### Exemple d'URL générée
```
https://nominatim.openstreetmap.org/search?format=json&limit=1&q=2%20boulevard%20de%20lavoisier%2C%2049100%2C%20angers%2C%20france
```

### 📝 Respect des règles Nominatim

1. **User-Agent obligatoire** : Identifie notre application
2. **Pas plus d'1 requête/seconde** : Respecté par design (usage pédagogique)
3. **Pas de cache persistant** : Chaque requête interroge le service

## 🔍 Dépannage

### Problèmes courants

| Problème | Cause probable | Solution |
|----------|---------------|----------|
| `Connection timed out` | Pas d'internet ou Nominatim indisponible | Vérifier la connexion, essayer le mode offline |
| `Aucun résultat pour...` | Adresse mal formatée ou inexistante | Simplifier l'adresse, essayer "ville pays" |
| `Build failed` | Java < 17 | Installer Java 17+ |
| Réponse vide | User-Agent manquant/invalide | Vérifier le User-Agent dans le code |

### Debug et logs

Pour voir les détails des requêtes HTTP, ajouter dans `ClientNominatim.java` :

```java
System.out.println("URL appelée : " + url);
System.out.println("Réponse HTTP : " + reponse.body());
```

## 📚 Ressources

- **Documentation Nominatim** : https://nominatim.org/release-docs/develop/api/Search/
- **OpenStreetMap** : https://www.openstreetmap.org/
- **Jackson (JSON)** : https://github.com/FasterXML/jackson
- **Java HttpClient** : https://docs.oracle.com/en/java/javase/17/docs/api/java.net.http/java/net/http/HttpClient.html