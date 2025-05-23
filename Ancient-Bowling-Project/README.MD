# Telemis - Exercice de developpement Java/Web

## Introduction

Ce projet implémente un jeu de bowling ancien fictif, présumé inspiré de découvertes archéologiques africaines. Il s'agit d'une application web moderne construite en deux phases principales :

1. Une implémentation du moteur de jeu en Java pur, suivant les principes de la POO
2. Une extension web RESTful utilisant Spring Boot

## Architecture de la Solution

### Phase 1 : Moteur de Jeu Core

Le moteur de jeu est construit autour de trois classes principales, comme illustré dans le diagramme UML :

#### 1. Classe `Game`
- Singleton gérant l'état global du jeu
- Gère la liste des joueurs et le tour de jeu
- Attributs principaux :
  - `players: List<Player>` - Liste des joueurs
  - `currentPlayerIndex: int` - Index du joueur actuel
  - `isStarted: boolean` - État du jeu

#### 2. Classe `Player`
- Représente un joueur et son état dans le jeu
- Gère les frames du joueur et calcule son score
- Attributs principaux :
  - `frames: List<Frame>` - Liste des frames du joueur
  - `currentFrame: Frame` - Frame actuelle
  - `name: String` - Nom du joueur

#### 3. Classe `Frame`
- Représente une frame individuelle
- Gère la logique des lancers et le calcul des points
- Attributs principaux :
  - `throwList: List<Integer>` - Liste des lancers
  - `isCompleted: boolean` - État de complétion
  - `isLastFrame: boolean` - Indique si c'est la dernière frame
  - `isStrike: boolean` - Indique un strike
  - `isSpare: boolean` - Indique un spare

### Phase 2 : Extension Web avec Spring Boot

L'application a été étendue avec une API REST complète :

#### Architecture MVC
- **Controllers** : `GameController` gère les endpoints REST
- **Services** : `GameService` adapte le moteur de jeu pour le web
- **DTOs** : 
  - `CreateGameRequest`
  - `ThrowRequest`
  - `GameResponse`
- **Exception Handling** : `GlobalExceptionHandler` pour la gestion des erreurs

## Technologies Utilisées

- **Java 17** : Langage principal, utilisant les fonctionnalités modernes
- **Maven** : Gestion des dépendances et build
- **Spring Boot 3.2.3** : Framework web
- **JUnit 5.10.2** : Tests unitaires du moteur de jeu
- **Mockito** : Tests des composants web
- **Swagger/OpenAPI** : Documentation automatique de l'API
- **Spring Validation** : Validation des entrées

## API REST

### Endpoints

| Méthode | Endpoint                                   | Description                          |
|--------:|:-------------------------------------------|:----|
| `POST`  | `/api/games`                               | Création d'une nouvelle partie        |
| `POST`  | `/api/games/{gameId}/players`              | Ajoute un joueur à la partie         |
| `POST`  | `/api/games/{gameId}/start`                | Démarre une partie existante         |
| `POST`  | `/api/games/{gameId}/throw`                | Enregistre un lancer                 |
| `GET`   | `/api/games/{gameId}`                      | Récupère l’état actuel de la partie  |
| `GET`   | `/api/games/{gameId}/scoreboard`           | Récupère le tableau des scores       |
## Tests

- Tests unitaires complets pour le moteur de jeu
- Tests d'intégration pour l'API REST
- Couverture des cas spéciaux (strikes, spares, dernière frame)

## Améliorations Futures Possibles

1. **WebSocket Integration**
   - Jeu en temps réel multi-joueurs
   - Notifications en direct des actions des joueurs

2. **Frontend SPA React**
   - Interface utilisateur moderne et responsive
   - Gestion d'état côté client avec Redux/MobX

3. **Persistence des Données**
   - Intégration d'une base de données H2
   - Stockage des profils joueurs et statistiques
   - Historique des parties

## Règles du Jeu

### Configuration
- 5 frames par joueur
- 3 lancers par frame
- 15 quilles à abattre
- Minimum 2 joueurs

### Système de Score
1. **Frame Normale** : Somme des quilles (max 14 points)
2. **Spare** : 15 points + 2 lancers suivants (max 45 points)
3. **Strike** : 15 points + 3 lancers suivants (max 60 points)
4. **Score Maximum** : 300 points

### Bonus
- Strike ou Spare à la dernière frame : lancers bonus
- Maximum 4 lancers dans la dernière frame

## Installation et Démarrage

```bash
# Cloner le repository
git clone [url-du-repo]

# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`
Documentation Swagger sur `http://localhost:8080/swagger-ui.html`