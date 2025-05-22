package org.telemis.bowling.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telemis.bowling.model.Game;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService gameService;
    private String gameId;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        gameId = gameService.createGame();
    }

    @Test
    void shouldNotAllowDuplicatePlayerNames() {
        String playerName = "John";
        
        // First addition should succeed
        assertDoesNotThrow(() -> gameService.addPlayer(gameId, playerName));
        
        // Second addition should fail
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> gameService.addPlayer(gameId, playerName));
        assertTrue(exception.getMessage().contains("Player with name 'John' already exists"));
    }

    @Test
    void shouldValidatePlayerName() {
        // Test null name
        assertThrows(IllegalArgumentException.class,
            () -> gameService.addPlayer(gameId, null));

        // Test empty name
        assertThrows(IllegalArgumentException.class,
            () -> gameService.addPlayer(gameId, ""));

        // Test whitespace name
        assertThrows(IllegalArgumentException.class,
            () -> gameService.addPlayer(gameId, "   "));
    }

    @Test
    void shouldTrimPlayerName() {
        String playerName = "  John  ";
        gameService.addPlayer(gameId, playerName);
        
        Game game = gameService.getGame(gameId);
        assertEquals("John", game.getPlayers().get(0).getName());
        
        // Should not allow adding the same name with different whitespace
        assertThrows(IllegalArgumentException.class,
            () -> gameService.addPlayer(gameId, "John"));
    }
} 