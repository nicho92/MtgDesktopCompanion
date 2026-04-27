# Pistes d'amélioration de rédaction (codebase)

## 1) Uniformiser la langue des identifiants et des messages
- Le code mélange des noms français (`panneauHaut`) et anglais (`splitCentral`, `detailsPanel`) dans une même classe.
- Recommandation : choisir une convention unique (anglais technique + i18n pour le texte UI).

## 2) Corriger les fautes dans les textes visibles utilisateur
- Plusieurs `tips` contiennent des formulations approximatives, ponctuation irrégulière et casse incohérente.
- Recommandation : relire avec un style guide court (phrases impératives, ponctuation propre, première lettre en majuscule).

## 3) Corriger les fautes dans logs/identifiants
- Exemple de log : `unknow edition`.
- Exemple d'identifiants avec orthographe non standard : `Buzy`, `HelpCompononent`, `langage`, `player-profil`.
- Recommandation : corriger progressivement en conservant une compatibilité (renommage IDE, alias de config si nécessaire).

## 4) Clarifier le ton des messages d'erreur
- Les logs utilisent parfois un message vague (`error loading tree`, `error loading help`).
- Recommandation : adopter un format homogène : action + contexte + identifiant métier + exception.

## 5) Industrialiser la qualité rédactionnelle
- Ajouter une étape CI légère (ex : dictionnaire custom + spell-check sur ressources et messages).
- Ajouter un mini guide de style dans `CONTRIBUTING.md` (langue, casse, ponctuation, terminologie).
