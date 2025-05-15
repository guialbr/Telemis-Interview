package org.telemis.bowling.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        System.out.println("DEBUG: GameTest.setUp");
        game = Game.getInstance();
        game.reset();
    }

    @Test
    void testCannotStartGameWithoutMinimumPlayers() {
        System.out.println("DEBUG: Start testCannotStartGameWithoutMinimumPlayers");
        game.addPlayer("Player 1");
        assertThrows(IllegalStateException.class, () -> game.start());
        game.addPlayer("Player 2");
        assertDoesNotThrow(() -> game.start());
        System.out.println("DEBUG: End testCannotStartGameWithoutMinimumPlayers");
    }

    @Test
    void testCannotAddPlayersAfterGameStart() {
        System.out.println("DEBUG: Start testCannotAddPlayersAfterGameStart");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        assertThrows(IllegalStateException.class, () -> game.addPlayer("Player 3"));
        System.out.println("DEBUG: End testCannotAddPlayersAfterGameStart");
    }

    @Test
    void testPlayerRotation() {
        System.out.println("DEBUG: Start testPlayerRotation");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        assertEquals("Player 1", game.getCurrentPlayer().getName());
        // Complete first frame for Player 1
        game.addThrow(7);
        game.addThrow(8); // Spare, frame complete
        assertEquals("Player 2", game.getCurrentPlayer().getName());
        // Complete first frame for Player 2
        game.addThrow(5);
        game.addThrow(5);
        game.addThrow(3); // Frame complete
        assertEquals("Player 1", game.getCurrentPlayer().getName());
        System.out.println("DEBUG: End testPlayerRotation");
    }

    @Test
    void testCompleteGameFlow() {
        System.out.println("DEBUG: Start testCompleteGameFlow");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        // Simulate a complete game
        for (int frame = 0; frame < 5; frame++) {
            for (int player = 0; player < 2; player++) {
                game.addThrow(5);
                game.addThrow(5);
                game.addThrow(3);
            }
        }
        assertTrue(game.isGameComplete());
        // Check final scores
        var scoreboard = game.getScoreboard();
        assertEquals(2, scoreboard.size());
        assertEquals("Player 1", scoreboard.get(0).name());
        assertEquals("Player 2", scoreboard.get(1).name());
        assertEquals(scoreboard.get(0).score(), scoreboard.get(1).score());
        System.out.println("DEBUG: End testCompleteGameFlow");
    }

    @Test
    void testBonusThrowsForLastFrame() {
        System.out.println("DEBUG: Start testBonusThrowsForLastFrame");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        // Complete 4 frames for both players
        for (int frame = 0; frame < 4; frame++) {
            for (int player = 0; player < 2; player++) {
                game.addThrow(5);
                game.addThrow(5);
                game.addThrow(3);
            }
        }
        // Player 1 gets a strike in last frame
        game.addThrow(15);
        // Player 2 completes last frame normally
        game.addThrow(5);
        game.addThrow(5);
        game.addThrow(5);
        // Player 1 should get bonus throws
        assertFalse(game.isGameComplete());
        assertEquals("Player 1", game.getCurrentPlayer().getName());
        // Complete bonus throws
        game.addThrow(10);
        game.addThrow(5);
        game.addThrow(3);
        assertTrue(game.isGameComplete());
        System.out.println("DEBUG: End testBonusThrowsForLastFrame");
    }

    @Test
    void testCannotThrowBeforeGameStarts() {
        System.out.println("DEBUG: Start testCannotThrowBeforeGameStarts");
        game.addPlayer("TestPlayer1");
        game.addPlayer("TestPlayer2");
        assertThrows(IllegalStateException.class, () -> game.addThrow(5));
        System.out.println("DEBUG: End testCannotThrowBeforeGameStarts");
    }

    @Test
    void testScoreChartExample1() {
        System.out.println("DEBUG: Start testScoreChartExample1");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2"); // Add a dummy player to satisfy min players
        game.start();
        // Player 1's throws (frame by frame):
        // Frame 1: 8, 1, 1
        game.addThrow(8); // 1st throw
        game.addThrow(1); // 2nd throw
        game.addThrow(1); // 3rd throw
        // Frame 2: 8, 7 (spare)
        game.addThrow(8); // 1st throw
        game.addThrow(7); // 2nd throw (spare, 8+7=15)
        // Frame 3: 1, 2, 1
        game.addThrow(1);
        game.addThrow(2);
        game.addThrow(1);
        // Frame 4: 15 (strike)
        game.addThrow(15);
        // Frame 5: 1, 2, 1
        game.addThrow(1);
        game.addThrow(2);
        game.addThrow(1);
        // Get Player 1's frames and scores
        Player player1 = game.getScoreboard().get(0).player();
        var frames = player1.getFrames();
        assertEquals(5, frames.size());
        // Check cumulative scores after each frame
        assertEquals(10, player1.calculateScore(1));
        assertEquals(28, player1.calculateScore(2));
        assertEquals(31, player1.calculateScore(3));
        assertEquals(50, player1.calculateScore(4));
        assertEquals(53, player1.calculateScore(5));
        System.out.println("DEBUG: End testScoreChartExample1");
    }
} 