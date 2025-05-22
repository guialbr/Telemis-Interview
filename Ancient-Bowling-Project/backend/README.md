# Ancient African Bowling Game - REST API

This is a REST API implementation of the Ancient African Bowling game. The game follows special rules where each frame starts with 15 pins, and players can get bonuses for strikes and spares.

## API Endpoints

### Create a New Game
```http
POST /api/games
```
Returns: Game ID (String)

### Add a Player
```http
POST /api/games/{gameId}/players?playerName={name}
```

### Start the Game
```http
POST /api/games/{gameId}/start
```

### Make a Throw
```http
POST /api/games/{gameId}/throw?pins={numberOfPins}
```
- `pins`: Number of pins knocked down (0-15)

### Get Game State
```http
GET /api/games/{gameId}
```
Returns: Current game state including players, scores, and current player's turn

## Game Rules
- Each frame starts with 15 pins
- Players take turns throwing
- A strike (all 15 pins) or spare (all remaining pins in second throw) awards bonus points
- Game consists of 5 frames per player
- Minimum 2 players required to start a game

## Example Usage with cURL

1. Create a new game:
```bash
curl -X POST http://localhost:8080/api/games
```

2. Add players:
```bash
curl -X POST "http://localhost:8080/api/games/{gameId}/players?playerName=Player1"
curl -X POST "http://localhost:8080/api/games/{gameId}/players?playerName=Player2"
```

3. Start the game:
```bash
curl -X POST http://localhost:8080/api/games/{gameId}/start
```

4. Make throws:
```bash
curl -X POST "http://localhost:8080/api/games/{gameId}/throw?pins=10"
```

5. Get game state:
```bash
curl http://localhost:8080/api/games/{gameId}
``` 