package org.telemis.bowling.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class that controls the overall bowling game flow.
 * <p>
 * It manages players, frames, turns, and score calculation.
 * Each game consists of 5 frames per player, with support for strike/spare bonus rules.
 * All 15 pins are reset at the start of each frame.
 * </p>
 */
public class Game {
    private static Game instance;
    private static final int MIN_PLAYERS = 2;
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean isStarted;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private Game() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.isStarted = false;
    }

    /**
     * Returns the singleton instance of the game.
     *
     * @return the {@code Game} instance
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /**
     * Resets the game to its initial state, clearing all players and scores.
     */
    public void reset() {
        players.clear();
        currentPlayerIndex = 0;
        isStarted = false;
    }

    /**
     * Starts the game if the minimum number of players has been added.
     *
     * @throws IllegalStateException if fewer than 2 players are present
     */
    public void start() {
        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException("Need at least " + MIN_PLAYERS + " players to start the game");
        }
        isStarted = true;
    }

    /**
     * Adds a new player to the game before it starts.
     *
     * @param name the name of the player to add
     * @throws IllegalStateException if the game has already started
     */
    public void addPlayer(String name) {
        if (isStarted) {
            throw new IllegalStateException("Cannot add any players after starting the game");
        }
        players.add(new Player(name));
    }

    /**
     * Records a throw for the current player, advancing to the next player
     * when the frame is completed.
     *
     * @param pins number of pins knocked down in this throw
     * @throws IllegalStateException if the game hasn't started or is already complete
     */
    public void addThrow(int pins) {
        if (!isStarted) {
            throw new IllegalStateException("Game has not started");
        } else if (isGameComplete()) {
            throw new IllegalStateException("Game is complete, cannot add more throws.");
        }
        Player currentPlayer = getCurrentPlayer();
        currentPlayer.ensureFreshFrame();
        currentPlayer.addThrow(pins);
        if (currentPlayer.getCurrentFrame().isCompleted()) {
            moveToNextPlayer();
        }
    }

    /**
     * Moves turn to the next player, skipping players who are already done
     * and accounting for bonus throws.
     */
    private void moveToNextPlayer() {
        int startIndex = currentPlayerIndex;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            if (currentPlayerIndex == startIndex) break;
        } while (isGameComplete() && !getCurrentPlayer().needsBonusThrows());
    }

    /**
     * Checks if all players have completed the game, including any bonus throws.
     *
     * @return {@code true} if the game is complete for all players; {@code false} otherwise
     */
    public boolean isGameComplete() {
        return players.stream().allMatch(Player::isGameComplete);
    }

    /**
     * Returns the current player whose turn it is to play.
     *
     * @return the {@link Player} object for the current turn
     * @throws IllegalStateException if no players have been added
     */
    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No players in the game");
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * Returns an unmodifiable list of all players in the game.
     *
     * @return a list of {@link Player} objects
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Checks if the game has been started.
     *
     * @return {@code true} if the game has started; {@code false} otherwise
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Returns a sorted scoreboard of players and their total scores.
     *
     * @return a list of {@link PlayerScore} records, sorted by descending score
     */
    public List<PlayerScore> getScoreboard() {
        List<PlayerScore> scores = new ArrayList<>();
        for (Player player : players) {
            scores.add(new PlayerScore(player.getName(), player.calculateScore(), player));
        }
        scores.sort((a, b) -> b.score() - a.score()); // Sort by descending order
        return scores;
    }

    /**
     * Immutable record representing a player's score entry in the scoreboard.
     *
     * @param name   the player's name
     * @param score  the player's total score
     * @param player the player object
     */
    public record PlayerScore(String name, int score, Player player) {}
} 