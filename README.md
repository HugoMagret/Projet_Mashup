1. Cloner un dépôt
# Avec SSH (clé configurée)
git clone git@github.com:HugoMagret/Projet_Mashup.git

# Avec HTTPS
git clone https://github.com/HugoMagret/Projet_Mashup.git

2. Vérifier l’état du projet
git status

3. Ajouter, commit et push
git add .                      # ajouter tous les fichiers modifiés
git commit -m "Message clair"  # créer un commit
git push                       # envoyer sur GitHub


Si le push est rejeté (branche distante a évolué) :

git pull --rebase   # récupère et rejoue vos commits sur la branche distante
git push

4. Créer et basculer sur une branche
git checkout -b ma-branche      # crée et bascule sur ma-branche
git checkout main               # revenir sur la branche principale

5. Lister les branches
git branch          # branches locales
git branch -r       # branches distantes

6. Fusionner une branche

Depuis main (ou une autre branche cible) :

git checkout main
git merge ma-branche
git push

7. Supprimer une branche
git branch -d ma-branche        # supprimer en local
git push origin --delete ma-branche   # supprimer sur GitHub

8. Récupérer les dernières modifs
git pull --rebase

9. Voir l’historique
git log --oneline --graph --decorate --all
