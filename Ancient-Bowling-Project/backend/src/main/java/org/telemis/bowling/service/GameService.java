package org.telemis.bowling.service;

import org.springframework.stereotype.Service;
import org.telemis.bowling.model.Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameService {

    // In-memory storage for games
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    private static final AtomicInteger gameCounter = new AtomicInteger();

    public String createGame() {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new Game());
        return gameId;
    }

    public Game getGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found with ID: " + gameId);
        }
        return game;
    }

    public void addPlayer(String gameId, String playerName) {
        Game game = getGame(gameId);
        game.addPlayer(playerName);
    }

    public void startGame(String gameId) {
        Game game = getGame(gameId);
        game.start();
    }

    public void addThrow(String gameId, int pins) {
        if (pins < 0 || pins > 15) {
            throw new IllegalArgumentException("Number of pins must be between 0 and 15");
        }
        Game game = getGame(gameId);
        game.addThrow(pins);
    }

    public void deleteGame(String gameId) {
        games.remove(gameId);
    }

    public Collection<Game> getAllGames() {
        return games.values();
    }

    // Simple response class for testing
    public static class GameResponse {
        private String gameId;
        private boolean gameStarted;
        private List<String> players;
        private String currentPlayer;
        private int currentFrame = 1;
        private Map<String, Integer> scores = new HashMap<>();

        // Getters and setters
        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public boolean isGameStarted() {
            return gameStarted;
        }

        public void setGameStarted(boolean gameStarted) {
            this.gameStarted = gameStarted;
        }

        public List<String> getPlayers() {
            return players;
        }

        public void setPlayers(List<String> players) {
            this.players = players;
        }

        public String getCurrentPlayer() {
            return currentPlayer;
        }

        public void setCurrentPlayer(String currentPlayer) {
            this.currentPlayer = currentPlayer;
        }

        public int getCurrentFrame() {
            return currentFrame;
        }

        public void setCurrentFrame(int currentFrame) {
            this.currentFrame = currentFrame;
        }

        public Map<String, Integer> getScores() {
            return scores;
        }

        public void setScores(Map<String, Integer> scores) {
            this.scores = scores;
        }

        public void addPlayer(String playerName) {
            if (gameStarted) {
                throw new RuntimeException("Cannot add players after game has started");
            }
            if (players.contains(playerName)) {
                throw new RuntimeException("Player already exists: " + playerName);
            }
            players.add(playerName);
            scores.put(playerName, 0);
        }

        public void start() {
            if (players.isEmpty()) {
                throw new RuntimeException("Cannot start game without players");
            }
            if (gameStarted) {
                throw new RuntimeException("Game already started");
            }
            gameStarted = true;
            currentPlayer = players.get(0);
        }

        public void addThrow(int pins) {
            if (!gameStarted) {
                throw new RuntimeException("Game not started");
            }
            // Simple implementation for testing
            int currentScore = scores.get(currentPlayer);
            scores.put(currentPlayer, currentScore + pins);
        }

        public Map<String, Object> getScoreboard() {
            Map<String, Object> scoreboard = new HashMap<>();
            scoreboard.put("gameId", gameId);
            scoreboard.put("currentFrame", currentFrame);
            scoreboard.put("currentPlayer", currentPlayer);
            scoreboard.put("scores", scores);
            return scoreboard;
        }

        public void reset() {
            gameStarted = false;
            players.clear();
            scores.clear();
            currentPlayer = null;
            currentFrame = 1;
        }
    }
}