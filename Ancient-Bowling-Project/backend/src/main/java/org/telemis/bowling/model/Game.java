package org.telemis.bowling.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Represent a singleton for the game controller instance in order to manage player and frame 
 * Once completed, all 15 pins are reset
 **/
public class Game {
    private static Game instance;
    private static final int MIN_PLAYERS = 2;
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean isStarted;
    
    //Game is a singleton
    private Game() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.isStarted = false;
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void reset() {
        players.clear();
        currentPlayerIndex = 0;
        isStarted = false;
    }

    public void start() {
        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException("Need at least " + MIN_PLAYERS + " players to start the game");
        }
        isStarted = true;
    }

    public void addPlayer(String name) {
        if (isStarted) {
            throw new IllegalStateException("Cannot add any players after starting the game");
        }
        players.add(new Player(name));
    }
    

    public void addThrow(int pins) {
        if (!isStarted) {
            throw new IllegalStateException("Game has not started");
        }

        Player currentPlayer = getCurrentPlayer();
        currentPlayer.addThrow(pins);

        if (currentPlayer.getCurrentFrame().isCompleted()) {
            moveToNextPlayer();
        }
    }

    private void moveToNextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (isGameComplete() && !getCurrentPlayer().needsBonusThrows());
    }
    
    public boolean isGameComplete() {
        return players.stream().allMatch(Player::isGameComplete);
    }
    
    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No players in the game");
        }
        return players.get(currentPlayerIndex);
    }
    
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }
    
    public boolean isStarted() {
        return isStarted;
    }
    
    public List<PlayerScore> getScoreboard() {
        List<PlayerScore> scores = new ArrayList<>();
        for (Player player : players) {
            scores.add(new PlayerScore(player.getName(), player.calculateScore(), player));
        }
        scores.sort((a, b) -> b.score() - a.score()); // Sort by score descending
        return scores;
    }
    
    // Record to represent player scores for the scoreboard
    public record PlayerScore(String name, int score, Player player) {}
} 