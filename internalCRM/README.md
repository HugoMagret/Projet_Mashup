# 📊 Module CRM Interne

## 📋 Description

Ce module implémente un **CRM (Customer Relationship Management) interne** qui stocke et filtre des prospects commerciaux. Il utilise **Apache Thrift** pour exposer ses services et permet de rechercher des prospects par critères (revenus, région, dates).

### ✨ Fonctionnalités

- **Stockage de prospects** : Nom, entreprise, chiffre d'affaires, coordonnées, région...
- **Recherche par revenus** : Trouver les prospects dans une fourchette de CA (ex: 50k€-150k€)
- **Recherche par région** : Filtrer par département/région (ex: "Loire-Atlantique")
- **Recherche par dates** : Prospects créés dans une période donnée
- **Format spécial** : Retourne les noms au format "Nom, Prénom"
- **Service Thrift** : Accessible en réseau par d'autres applications

### 🎯 Exemple concret

```
Ajout    : Jean Dupont, Acme, 50k€, Maine-et-Loire
Recherche: findLeads(45000, 60000, "Maine-et-Loire")
Résultat : [{ firstName: "Dupont, Jean", revenue: 50000, ... }]
```

## 🏗️ Architecture

```
internalCRM/
├── src/main/java/org/example/internal/
│   ├── InternalCRMHandler.java     # Logique métier (stockage, recherche)
│   ├── InternalCRMServer.java      # Serveur Thrift
│   └── InternalCRMDemo.java        # Démo locale sans réseau
├── src/main/thrift/
│   └── internalcrm.thrift          # Définition Thrift (contrat)
├── gen-java/                       # Classes générées par Thrift
│   └── org/example/internal/
│       ├── InternalCRM.java        # Interface service générée
│       └── InternalLeadDTO.java    # Structure de données générée
├── build.gradle                    # Configuration du build
└── README.md                       # Ce fichier
```

### 📦 Classes principales

| Classe | Rôle | Description |
|--------|------|-------------|
| `InternalCRMHandler` | Logique métier | Stockage en mémoire, filtrage, CRUD prospects |
| `InternalCRMServer` | Serveur Thrift | Expose le service sur le port 9090 |
| `InternalCRMDemo` | Démo locale | Test du handler sans réseau |
| `InternalLeadDTO` | Structure prospect | Nom, revenus, adresse, région... (généré) |
| `InternalCRM` | Interface service | Méthodes exposées (généré) |
| `model/Lead` | Entité domaine | Représente un prospect (classe métier interne)
| `model/LeadModel` | Interface modèle | Contrat d'accès au stockage (CRUD, recherches)
| `model/LeadModelImpl` | Impl. mémoire | Implémentation thread-safe en mémoire
| `model/exception/*` | Exceptions métier | Exceptions Java côté modèle (utiles en demo)
| `utils/ConverterUtils` | Conversion | Convertit entre `Lead` (modèle) et `InternalLeadDTO` (Thrift)
| `service/InternalServiceImpl` | Implémentation Thrift | Implémentation de `InternalCRM.Iface` qui valide et lance les exceptions Thrift
| `service/ThriftInternalServiceServlet` | Servlet Thrift HTTP | Expose le service Thrift via HTTP (TServlet wrapper)
| `service/ThriftHttpServletTemplate` | Template servlet | Petite classe réutilisable pour TServlet

## 🚀 Compilation et Exécution

### Prérequis
- **Java 17+**
- **Gradle** (inclus via wrapper)
- **Apache Thrift** (dépendance automatique via Gradle)

### 🔨 Compilation

```bash
# Depuis la racine du projet
./gradlew :internalCRM:build

# Ou depuis le dossier internalCRM/
../gradlew build
```

⚠️ **Note** : Les classes Java sont générées automatiquement depuis `internalcrm.thrift` et se trouvent dans `gen-java/`.

### ⚡ Démarrage du serveur

```bash
# Serveur sur le port par défaut (9090)
./gradlew :internalCRM:runInternalCRMServer

# Serveur sur un port personnalisé
./gradlew :internalCRM:runInternalCRMServer -Pport=8080
```

**Résultat attendu :**
```
[InternalCRM] Serveur démarré sur le port 9090. Ctrl+C pour arrêter.
```

Le serveur reste ouvert et attend les connexions clients Thrift.

## 🧪 Tests et Démonstration

### 🎮 Démo locale (sans réseau)

Pour tester la logique métier sans démarrer le serveur :

```bash
./gradlew :internalCRM:runInternalCRMDemo
```

**Ce que fait la démo :**
1. Crée un prospect "Alice Martin" (120k€, Loire-Atlantique)
2. Cherche tous les prospects entre 100k€ et 130k€ en Loire-Atlantique
3. Affiche les résultats trouvés

**Résultat attendu :**
```
[DEMO] Prospects trouvés (100k-130k€, Loire-Atlantique) = 1
  -> Martin, Alice
[DEMO] Test terminé avec succès !
```

### 🌐 Test du serveur avec client Thrift

Une fois le serveur démarré, vous pouvez vous y connecter depuis d'autres modules :

```java
// Exemple de client Thrift (dans un autre module)
TTransport transport = new TSocket("localhost", 9090);
TProtocol protocol = new TBinaryProtocol(transport);
InternalCRM.Client client = new InternalCRM.Client(protocol);

transport.open();
List<InternalLeadDTO> results = client.findLeads(50000, 100000, "Maine-et-Loire");
transport.close();
```

## 🔧 API du Service

### 📋 Méthodes disponibles

#### 1. `findLeads(lowRevenue, highRevenue, state)`
Trouve les prospects dans une fourchette de revenus et/ou région.

```java
// Exemples d'appels
findLeads(50000, 150000, null)                    // 50k€-150k€, toutes régions
findLeads(0, 100000, "Loire-Atlantique")          // 0-100k€ en Loire-Atlantique
findLeads(75000, 75000, "Maine-et-Loire")         // Exactement 75k€ en Maine-et-Loire
```

#### 2. `findLeadsByDate(fromIso, toIso)`
Trouve les prospects créés dans une période.

```java
// Exemples d'appels
findLeadsByDate("2024-01-01T00:00:00Z", "2024-12-31T23:59:59Z")  // Toute l'année 2024
findLeadsByDate("2024-09-01T00:00:00Z", null)                    // Depuis septembre 2024
findLeadsByDate(null, "2024-06-30T23:59:59Z")                    // Jusqu'à juin 2024
```

#### 3. `createLead(leadDto)`
Ajoute un nouveau prospect.

```java
InternalLeadDTO nouveau = new InternalLeadDTO();
nouveau.setFirstName("Marie");
nouveau.setLastName("Dubois");
nouveau.setAnnualRevenue(85000);
nouveau.setState("Vendée");
nouveau.setCreationDate("2024-10-07T10:00:00Z");

long id = createLead(nouveau);  // Retourne l'ID généré (1, 2, 3...)
```

#### 4. `deleteLead(leadDto)`
Supprime un prospect.

```java
deleteLead(prospectASupprimer);  // Supprime tous les prospects identiques
```

### 📊 Structure InternalLeadDTO

| Champ | Type | Description | Exemple |
|-------|------|-------------|---------|
| `firstName` | String | Prénom | "Jean" |
| `lastName` | String | Nom de famille | "Dupont" |
| `annualRevenue` | double | Chiffre d'affaires annuel | 75000.0 |
| `companyName` | String | Nom de l'entreprise | "Acme SARL" |
| `phone` | String | Téléphone | "+33123456789" |
| `street` | String | Adresse | "1 rue Exemple" |
| `postalCode` | String | Code postal | "49100" |
| `city` | String | Ville | "Angers" |
| `state` | String | Région/Département | "Maine-et-Loire" |
| `country` | String | Pays | "France" |
| `creationDate` | String | Date création (ISO-8601) | "2024-10-07T10:00:00Z" |

## ⚠️ Particularités importantes

### 🔄 Format des noms retournés

**RÈGLE SPÉCIALE** : Les prospects retournés par les recherches ont leurs noms fusionnés au format `"Nom, Prénom"`.

```java
// Lors de la création
leadDto.setFirstName("Jean");
leadDto.setLastName("Dupont");

// Lors du retour de recherche
result.getFirstName();  // → "Dupont, Jean"
result.getLastName();   // → ""
```

### 💾 Stockage en mémoire

- Les données sont **perdues au redémarrage** du serveur
- Un prospect d'exemple (Jean Dupont) est créé automatiquement au démarrage
- Pour une version production, remplacer par une vraie base de données

### 🔒 Thread-safety

Le handler utilise `ConcurrentHashMap` et `AtomicLong`, il est donc **thread-safe** et peut gérer plusieurs clients simultanément.

## 🛑 Exceptions Thrift ajoutées

Le fichier Thrift (`src/main/thrift/internalcrm.thrift`) définit désormais plusieurs exceptions spécifiques. Elles sont générées dans `gen-java/` et doivent être gérées par les clients et le service :

- `ThriftNoSuchLeadException` : lever lorsque l'entité demandée n'existe pas
- `ThriftWrongDateFormatException` : lever lorsque le format de date fourni n'est pas ISO-8601
- `ThriftWrongOrderForDateException` : lever lorsque `from` > `to` dans une recherche par date
- `ThriftWrongOrderForRevenueException` : lever lorsque `low` > `high` dans une recherche par revenus
- `ThriftWrongStateException` : lever lorsque la valeur de `state` est invalide (format ou liste blanche si applicable)

Ces exceptions sont des types Thrift (générés) et sont lancées par `service/InternalServiceImpl` en cas d'erreurs de validation.

## 🔌 ConverterUtils

`utils/ConverterUtils` fournit deux méthodes utilitaires :

- `toDto(Lead)` : transforme l'entité métier interne en `InternalLeadDTO` (prépare le format attendu par Thrift)
- `fromDto(InternalLeadDTO)` : crée une instance `Lead` à partir d'un DTO Thrift

Ces méthodes centralisent les règles de conversion (par ex. format des noms, nettoyage des champs) et évitent la duplication de logique dans le service.

## 🌐 Servlet HTTP Thrift (optionnel)

Le projet contient `service/ThriftInternalServiceServlet` : un wrapper qui instancie un `TServlet` (Thrift-over-HTTP). Cela permet d'héberger le service Thrift via un conteneur web (Tomcat, Jetty) au lieu d'un serveur socket.

### Exemple d'usage (déploiement)

1. Packager le module en JAR et déployer le `internalCRM` avec un conteneur servlet.
2. Le endpoint exposé est `/thrift/internalcrm` (par défaut) ; envoyer des requêtes Thrift binaire vers cette URL.

> Remarque : pour compiler le code servlet sans runtime servlet, la dépendance `javax.servlet:javax.servlet-api:4.0.1` est déclarée `compileOnly`.

## 🔁 Notes de maintenance

- Les classes générées par Thrift (`gen-java/`) ne doivent pas être modifiées à la main : régénérez-les depuis `src/main/thrift` si vous changez l'IDL.
- Les modifications récentes ont ajouté : `ConverterUtils`, `service/InternalServiceImpl`, `service/ThriftInternalServiceServlet`, `service/ThriftHttpServletTemplate`, et les exceptions Thrift dans l'IDL.

## ✅ Vérifications effectuées

- Compilation `:internalCRM:build` réussie après ajustement de la dépendance servlet.
- Correction d'un problème de source (`LeadModelImpl.java`) qui contenait une insertion accidentelle.

---

Si tu veux, j'ajoute un petit extrait d'exemple montrant comment appeler le servlet HTTP (curl ou client Thrift) ou je peux mettre à jour le README racine pour résumer ces changements.

## 🐛 Dépannage

### Problèmes courants

| Problème | Cause probable | Solution |
|----------|---------------|----------|
| `Port already in use` | Serveur déjà démarré | Arrêter l'ancien serveur (Ctrl+C) ou changer de port |
| `Connection refused` | Serveur pas démarré | Lancer `runInternalCRMServer` avant le client |
| `Build failed: thrift` | Classes générées manquantes | Vérifier que `gen-java/` contient les classes |
| Pas de résultats | Critères trop restrictifs | Vérifier les fourchettes de revenus/dates |

### 📊 Données de test

Au démarrage, le serveur contient **1 prospect par défaut** :

```
Nom: Jean Dupont
Entreprise: Acme
Revenus: 50 000€
Région: Maine-et-Loire
Date: 2024-09-01T10:00:00Z
```

### 🔍 Debug

Pour voir les prospects en mémoire, ajouter dans `InternalCRMHandler` :

```java
public void debugPrintAll() {
    System.out.println("=== PROSPECTS EN MÉMOIRE ===");
    store.values().forEach(lead -> 
        System.out.println(lead.getFirstName() + " " + lead.getLastName() + 
                          " (" + lead.getAnnualRevenue() + "€)")
    );
}
```

## 🔗 Intégration avec d'autres modules

Ce module CRM interne peut être utilisé par :

- **Module VirtualCRM** : Pour récupérer les prospects internes
- **Autres services** : Via client Thrift standard
- **Tests automatisés** : Via l'instance `InternalCRMHandler` directement

### Exemple d'intégration

```java
// Dans un autre module
TTransport transport = new TSocket("localhost", 9090);
TProtocol protocol = new TBinaryProtocol(transport);
InternalCRM.Client crmClient = new InternalCRM.Client(protocol);

try {
    transport.open();
    List<InternalLeadDTO> prospects = crmClient.findLeads(50000, 200000, null);
    // Traiter les prospects...
} finally {
    transport.close();
}
```

## 📚 Ressources

- **Apache Thrift** : https://thrift.apache.org/
- **Documentation Thrift Java** : https://thrift.apache.org/docs/
- **Gradle Thrift Plugin** : Pour génération automatique des classes
- **ConcurrentHashMap** : https://docs.oracle.com/javase/17/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html