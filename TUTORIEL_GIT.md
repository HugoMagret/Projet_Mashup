# Projet_Mashup

------------------------------------------------------------------------

## 🚀 Cloner le dépôt

``` bash
git clone git@github.com:TonPseudo/projet_mashup.git
```

------------------------------------------------------------------------

## 🛠️ Travailler sur une issue

Une **issue** correspond à une *feature* (fonctionnalité) ou à un *bug*
à développer.

### Étapes à suivre :

1.  **Se placer sur la branche principale**

    ``` bash
    git checkout main
    ```

2.  **Mettre à jour la branche principale**

    ``` bash
    git fetch origin
    git pull
    ```

3.  **Créer une nouvelle branche pour l'issue**

    ``` bash
    git checkout -b issue_xxx
    ```

4.  **Pousser la branche distante** (nécessaire pour créer une Merge
    Request)

    ``` bash
    git push --set-upstream origin issue_xxx
    ```

5.  **Développer ta feature ou correction**

    -   Vérifier les fichiers modifiés :

        ``` bash
        git status
        ```

    -   Ajouter les fichiers à valider :

        ``` bash
        git add fichier1.html fichier2.ts
        ```

    -   Écrire un message de commit clair et concis :

        ``` bash
        git commit -m "Liste de films : ajout d’une barre de recherche"
        ```

6.  **Pousser les changements sur ta branche**

    ``` bash
    git push
    ```

7.  **Créer une Merge Request** liée à ton issue.\
    (Une *Merge Request* est une proposition de modification du code.)


------------------------------------------------------------------------
# 🚀 Lancer l’application en local (mode développement)

## 1️⃣ Se placer sur la branche principale

```bash
git checkout main
```

---

## 2️⃣ Installer Docker (Ubuntu/Debian)

1. Mettre à jour le système  
   ```bash
   sudo apt update && sudo apt upgrade -y
   ```
2. Installer les dépendances  
   ```bash
   sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
   ```
3. Ajouter la clé GPG de Docker  
   ```bash
   curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker.gpg
   ```
4. Ajouter le dépôt Docker  
   ```bash
   echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
   ```
5. Installer Docker  
   ```bash
   sudo apt update
   sudo apt install -y docker-ce docker-ce-cli containerd.io
   ```
6. Ajouter votre utilisateur au groupe `docker`  
   ```bash
   sudo usermod -aG docker $USER
   ```
7. **Redémarrer la session** (se déconnecter/reconnecter)  
   ```bash
   exit
   ```

---

## 3️⃣ Installer Docker Compose

### Option A — via `apt` (si disponible sur votre distribution)

```bash
sudo apt install -y docker-compose
```

### Option B — installation manuelle (dernière version)

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

---

## 4️⃣ Vérifier l’installation

```bash
docker --version
docker-compose --version    # ou: docker compose version
```

---

## 5️⃣ Lancer l’application

Depuis **la racine du projet** (là où se trouve `docker-compose.yml`) :

```bash
docker-compose --profile dev up -d
```

---

## 6️⃣ Dépannage (Auth Google)

Si l’authentification Google ne fonctionne pas, exécutez la séquence suivante pour **reconstruire les dépendances PHP** et redémarrer proprement :

```bash
# Arrêter les services
docker-compose down

# Nettoyer et réinstaller les dépendances backend (conteneur "api")
docker-compose run --rm api rm -rf /var/www/html/vendor
docker-compose run --rm api ls -la /var/www/html/composer.json
docker-compose run --rm api composer install --no-cache --optimize-autoloader
docker-compose run --rm api ls -la /var/www/html/vendor/autoload.php

# Redémarrer en profil dev
docker-compose --profile dev down && docker-compose --profile dev rm && docker-compose --profile dev up -d
```

> 💡 **Notes**
> - Ces commandes supposent que le service backend s’appelle **`api`** dans `docker-compose.yml` et que **Composer** est disponible dans l’image.
> - Elles suppriment puis réinstallent `vendor/`, utile si des erreurs PHP (autoload/mbstring) empêchent l’auth.
