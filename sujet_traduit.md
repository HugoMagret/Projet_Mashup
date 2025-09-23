# Projet : Mashup (Architecture distribuée)

|------------------------------------------------------|
|  LE PDF DU RPOF CONTIENT DES SHEMAS SUPPLEMENTAIRES  |
|------------------------------------------------------|

## 📌 Introduction
Ce projet consiste à développer une application qui combine différentes technologies de services web (**REST** et **RPC/Thrift**).  
L’objectif est de créer un **Mashup** : une application web construite en couches, qui s’appuie sur plusieurs services distants pour fournir ses fonctionnalités.  

---

## 🧩 Partie 1 : VirtualCRMService et ses composants

### 🔹 Contexte
- L’entreprise possède un **CRM interne** (développé maison).  
- Après un rachat, elle doit aussi gérer un **CRM Salesforce** (externe via API REST).  
- Objectif : créer un service intermédiaire **VirtualCRMService** qui donne une **vue unifiée des clients**.  

### 🔹 VirtualCRMService
- Fournit deux opérations principales :
  - `findLeads(revenueMin, revenueMax, province)`  
  - `findLeadsByDate(dateMin, dateMax)`  
- Retourne une liste de **VirtualLeadDto** avec :  
  - Nom, prénom, société, revenu annuel attendu  
  - Téléphone, adresse complète, pays  
  - Date d’enregistrement  
  - Coordonnées GPS (ou `null` si non trouvées)  

👉 Les résultats sont **triés par revenu décroissant**.  
👉 Le client VirtualCRMService doit être un **Singleton** (via Factory).  

### 🔹 Services utilisés
1. **InternalCRM (RPC/Thrift)**  
   - Fournit des opérations pour gérer les leads.  
   - Retourne un `InternalLeadDto` (nom complet dans un seul champ).  
   - Permet aussi de créer et supprimer des leads.  

2. **Salesforce (REST)**  
   - Accessible via **API REST** et langage de requête **SOQL**.  
   - Authentification requise (token géré automatiquement).  
   - Utilisation d’un **compte développeur Salesforce** avec données fictives.  

3. **Service de géolocalisation (REST)**  
   - Utilisation de l’API **Nominatim (OpenStreetMap)**.  
   - Prend une adresse et renvoie latitude/longitude au format JSON ou XML.  
   - Exemple :  
     ```
     https://nominatim.openstreetmap.org/search?city=angers&country=france&postalcode=49100&street=2+boulevard+de+lavoisier&format=json&limit=1
     ```

4. **Client (CLI)**  
   - Application en ligne de commande.  
   - Interroge l’API REST de VirtualCRMService.  
   - Affiche la liste des leads en texte dans la console.  

---

## 🧩 Partie 2 : Outil de fusion (InternalCRM + Salesforce)
À long terme, l’entreprise veut tout centraliser dans le **CRM interne**.  
- Développer un outil **en ligne de commande** qui :  
  - Récupère tous les leads de Salesforce (via REST).  
  - Les insère dans l’InternalCRM (via RPC/Thrift).  

---

## ✅ Livrables attendus
- **Code source** : compilable avec Gradle.  
- **Rapport PDF** avec :  
  - Diagrammes UML (classe + séquence).  
  - Architecture des modules.  
  - Explications claires du design.  
- **Archive `.zip` ou `.tar.gz`** à rendre sur Moodle.  

---

## 📝 Règles et contraintes
- Travail en groupe de **3 personnes**.  
- Respecter les **conventions de code Java** ([Java Code Conventions 1997](https://www.oracle.com/technetwork/java/codeconventions-150003.pdf)).  
- Utilisation obligatoire d’un dépôt Git (GitHub/GitLab).  
- Pas besoin de JavaDoc détaillé.  

---

## ⚙️ Rapport final
1. **Conception**  
   - UML : diagramme de classes et séquences.  
   - Explications des modules et packages.  

2. **Compilation / Installation**  
   - Étapes simples pour compiler et exécuter avec Gradle.  
   - Instructions claires pour lancer les services et clients.  

3. **Problèmes connus**  
   - Liste des bugs ou limitations identifiés.  

---

## 🔄 Itérations
- **Itération 1** : version intermédiaire (non notée).  
- **Itération 2** : version finale (notée).  

---

## 🎯 Critères d’évaluation
- Fonctionnement du projet.  
- Qualité du design.  
- Qualité du rapport.  
- ⚠️ Toute copie = échec pour les deux groupes (copieur et copié).  

---
