# Module CRM Interne

## Description

Service Thrift RPC pour gérer les prospects commerciaux (création, recherche, suppression).

**Exigence énoncé** : Section 2.2 - Service InternalCRM avec Apache Thrift

## Architecture

```
internalCRM/
├── src/main/java/org/example/internal/
│   ├── model/                    # Couche métier
│   │   ├── Lead.java             # Entité prospect (Calendar pour dates)
│   │   ├── LeadModel.java        # Interface
│   │   ├── LeadModelImpl.java    # Implémentation ConcurrentHashMap
│   │   └── exception/            # Exceptions métier
│   ├── service/
│   │   └── InternalServiceImpl.java  # Implémentation Thrift
│   ├── utils/
│   │   └── ConverterUtils.java   # Lead <-> DTO + ISO-8601
│   ├── InternalCRMHandler.java   # Handler legacy
│   ├── InternalCRMServer.java    # Serveur Thrift (port 9090)
│   └── InternalCRMDemo.java      # Démo locale
├── src/main/thrift/
│   └── internalcrm.thrift        # Contrat IDL Thrift
└── gen-java/                     # Classes générées par Thrift
```

## Utilisation dans VirtualCRM

VirtualCRM utilise ce module via **RPC Thrift** (réseau, pas de code partagé) :

```java
// Dans VirtualCRM - Client Thrift distant
TTransport transport = new TSocket("localhost", 9090);  // Connexion réseau
TProtocol protocol = new TBinaryProtocol(transport);
InternalCRM.Client client = new InternalCRM.Client(protocol);

transport.open();
List<InternalLeadDTO> leads = client.findLeads(50000, 150000, "Loire-Atlantique");
transport.close();
```

**Configuration Gradle VirtualCRM** :
```gradle
dependencies {
    // Seulement les classes Thrift générées (pas le code métier)
    implementation files('../internalCRM/gen-java')
    implementation 'org.apache.thrift:libthrift:0.16.0'
}
```

**Démarrage serveur** (requis avant d'utiliser le client) :
```bash
./gradlew :internalCRM:runInternalCRMServer  # Port 9090
```

## API Thrift

### Méthodes disponibles

```thrift
// Fichier: internalcrm.thrift
service InternalCRM {
  list<InternalLeadDTO> findLeads(1:i64 low, 2:i64 high, 3:string state)
  list<InternalLeadDTO> findLeadsByDate(1:string from, 2:string to)
  i64 createLead(1:InternalLeadDTO lead)
  void deleteLead(1:InternalLeadDTO template)
}
```

### Structure InternalLeadDTO

```thrift
struct InternalLeadDTO {
  1: string firstName,      // "Jean" → retourné comme "Dupont, Jean"
  2: string lastName,       // "Dupont"
  3: double annualRevenue,  // 50000.0
  4: string state,          // "Maine-et-Loire"
  5: string creationDate,   // "2024-09-15T10:00:00Z" (ISO-8601)
  // + 6 autres champs (company, phone, street, city, postal, country)
}
```

### Particularités

**Format "Nom, Prénom"** (exigence 2.2) :
```java
// Création
lead.setFirstName("Jean");
lead.setLastName("Dupont");

// Retour recherche (format inversé)
result.getFirstName();  // → "Dupont, Jean"
result.getLastName();   // → ""
```

**Type i64** : Entier 64 bits pour IDs (évite dépassements, 2^63 valeurs)

**Format ISO-8601** : `yyyy-MM-dd'T'HH:mm:ss'Z'` (ex: "2024-09-15T10:00:00Z")
- Conversion automatique String ↔ Calendar dans `ConverterUtils`

## Tests

```bash
# 1. Compiler (génère classes Thrift)
./gradlew :internalCRM:build

# 2. Démo locale (sans serveur)
./gradlew :internalCRM:runInternalCRMDemo

# 3. Démarrer serveur Thrift
./gradlew :internalCRM:runInternalCRMServer

# 4. Test client (depuis autre terminal/module)
# Voir exemple Java ci-dessus
```

**Résultat démo** :
```
[ÉTAPE 1] Création de 3 prospects
OK - Alice Martin   -> ID 2 (120 000 euros, Loire-Atlantique)
[ÉTAPE 2] Recherche par revenus (50k-100k€)
   -> Dupont, Jean | 50 000 euros  ← Format "Nom, Prénom" vérifié
[ÉTAPE 3] Recherche par dates (août-octobre 2024)
   -> Martin, Alice | créé le : 2024-09-15T10:00:00Z
```

## Gestion des erreurs

Exceptions Thrift (définies dans IDL) :

- `ThriftNoSuchLeadException` : Prospect introuvable (deleteLead)
- `ThriftWrongOrderForRevenueException` : low > high
- `ThriftWrongOrderForDateException` : from > to
- `ThriftWrongDateFormatException` : Date pas ISO-8601
- `ThriftWrongStateException` : État invalide

**Conversion** : `InternalServiceImpl` convertit exceptions Java → Thrift

## Où trouver chaque exigence (Section 2.2)

| Exigence | Fichier | Description |
|----------|---------|-------------|
| 2.2.1 RPC Thrift | `internalcrm.thrift` | Définition service + structures |
| 2.2.1 Serveur | `InternalCRMServer.java` | TSimpleServer port 9090 |
| 2.2.2 Délégation modèle | `InternalServiceImpl.java` | Appelle `LeadModelFactory.getModel()` |
| 2.2.2 Format "Nom, Prénom" | `LeadModelImpl.copyForReturn()` | Ligne 139-148 |
| 2.2.3 Masquage type | `ConverterUtils.java` | Lead ↔ InternalLeadDTO |
| 2.2.4 createLead | `InternalServiceImpl.java` | Ligne 77-83 |
| 2.2.4 deleteLead | `InternalServiceImpl.java` | Ligne 91-97 |
| 2.2.5 Exceptions | `internalcrm.thrift` + `InternalServiceImpl` | Conversion Java → Thrift |

## Stockage

- **En mémoire** : `ConcurrentHashMap<Long, Lead>` (thread-safe)
- **Données perdues** au redémarrage
- **1 prospect par défaut** : Jean Dupont (50k€, Maine-et-Loire)

## Dépannage

| Problème | Solution |
|----------|----------|
| Port 9090 déjà utilisé | `./gradlew :internalCRM:runInternalCRMServer -Pport=8080` |
| Connection refused | Démarrer le serveur avant le client |
| Build failed: thrift | Vérifier `gen-java/` contient classes générées |