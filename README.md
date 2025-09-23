📥 Cloner le dépôt
# SSH (préférable si ta clé est configurée)
"sudo" git clone git@github.com:HugoMagret/Projet_Mashup.git

# HTTPS
git clone https://github.com/HugoMagret/Projet_Mashup.git

🧭 Workflow recommandé (feature branch)

Se placer sur la branche principale et la mettre à jour :

git checkout main
git fetch origin
git pull --rebase origin main


Créer une branche pour l'issue / feature :

git checkout -b feat/nom-court
# ou
git switch -c feat/nom-court


Travailler, vérifier les changements, préparer le commit :

git status
git add fichier1 fichier2         # ou: git add .
git commit -m "Courte description : ce qui a été fait"


Pousser la branche distante (une seule fois pour relier local <-> remote) :

git push --set-upstream origin feat/nom-court
# ensuite, simplement :
git push


Ouvrir une Pull Request / Merge Request via l’interface GitHub.

🔁 Mettre à jour une branche feature avec main

Option propre (rebase) — historique linéaire :

git fetch origin
git rebase origin/main
# résoudre conflits si nécessaire, puis:
git rebase --continue


Option simple (merge) — commit de merge :

git fetch origin
git merge origin/main
# résoudre conflits, commit si requis

🔧 Résolution de conflits (cas courant)

Git signale les conflits après merge ou rebase.

Éditer les fichiers conflictuels, garder la version souhaitée.

Marquer comme résolu :

git add fichier_conflit
# si rebase :
git rebase --continue
# si merge :
git commit    # si git n'a pas créé automatiquement le commit de merge


Pour annuler un rebase en cours :

git rebase --abort

⚠️ Forcer un push (utiliser avec prudence)

N’écrasez le remote que si vous savez ce que vous faites.
Préférer --force-with-lease à --force :

git push --force-with-lease

🗂 Supprimer une branche
# supprimer localement
git branch -d feat/nom-court    # refuse si non mergée
git branch -D feat/nom-court    # force la suppression

# supprimer sur remote
git push origin --delete feat/nom-court

🧰 Commandes utiles (rappel rapide)
git fetch origin                              # récupérer les refs distantes
git pull --rebase                             # pull + rebase (évite commits de merge)
git status                                    # état du working tree
git diff                                      # voir les différences non-stagées
git add -p                                    # ajouter par hunks
git commit --amend -m "nouveau message"       # modifier dernier commit (local)
git log --oneline --graph --decorate --all    # historique compact
git stash                                     # sauvegarder temporairement les changements
git stash pop                                 # réappliquer le stash
git reset --soft HEAD~1                        # retirer le dernier commit, garder changements
git reset --hard HEAD                          # rétablir l'état exact du HEAD (perdre modifications locales)
git cherry-pick <commit>                       # appliquer un commit précis sur la branche courante

🔒 Bonnes pratiques

Faire des commits atomiques et messages explicites.

Travailler sur des branches nommées clairement (feat/, fix/, chore/).

Mettre à jour main avant de démarrer une feature (git pull --rebase).

Préférer rebase pour garder un historique lisible, utiliser merge si tu veux conserver le contexte de merge.

Ne pas forcer le push sur une branche partagée sans prévenir l’équipe.

Utiliser --force-with-lease si un force est nécessaire.

🧾 Exemple complet — cycle typique
# cloner
git clone git@github.com:HugoMagret/Projet_Mashup.git
cd Projet_Mashup

# préparer le travail
git checkout main
git pull --rebase origin main

# créer la branche
git checkout -b feat/ajout-auth

# coder, vérifier, committer
git status
git add .
git commit -m "feat(auth): ajout login via token"

# mettre à jour depuis main si besoin
git fetch origin
git rebase origin/main

# pousser et ouvrir PR
git push --set-upstream origin feat/ajout-auth
