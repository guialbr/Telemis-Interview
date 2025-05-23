# API REST – Jeu de Quilles Africain Ancien

Cette API REST permet de gérer une version traditionnelle du jeu de quilles africain. Chaque manche débute avec **15 quilles**, et des **bonus** sont attribués en cas de **strike** ou **spare**, selon des règles spécifiques.

## Fonctionnalités

- Création de parties
- Ajout de joueurs
- Lancement d’une partie
- Gestion des lancers
- Consultation de l’état de la partie
- Affichage du tableau des scores

## Endpoints disponibles

| Méthode | Endpoint                                   | Description                          |
|--------:|:-------------------------------------------|:-------------------------------------|
| `POST`  | `/api/games`                               | Création d'une nouvelle partie             |
| `POST`  | `/api/games/{gameId}/players`              | Ajoute un joueur à la partie         |
| `POST`  | `/api/games/{gameId}/start`                | Démarre une partie existante         |
| `POST`  | `/api/games/{gameId}/throw`                | Enregistre un lancer                 |
| `GET`   | `/api/games/{gameId}`                      | Récupère l’état actuel de la partie  |
| `GET`   | `/api/games/{gameId}/scoreboard`           | Récupère le tableau des scores       |

## Documentation Swagger

Une documentation interactive est accessible une fois le projet compilé et lancé :

- **URL locale** : [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Elle permet de consulter et de tester tous les endpoints disponibles.