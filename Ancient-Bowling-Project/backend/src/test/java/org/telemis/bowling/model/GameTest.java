package org.telemis.bowling.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * GameTest - Unit and integration tests for the Game class.
 * Each test is named and commented to clearly explain its purpose.
 */
class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        System.out.println("DEBUG: GameTest.setUp");
        game = Game.getInstance();
        game.reset();
    }

    /**
     * Test that the game cannot start with fewer than the minimum number of players.
     */
    @Test
    void cannotStartGameWithFewerThanMinPlayers() {
        System.out.println("DEBUG: Start cannotStartGameWithFewerThanMinPlayers");
        game.addPlayer("Player 1");
        assertThrows(IllegalStateException.class, () -> game.start());
        game.addPlayer("Player 2");
        assertDoesNotThrow(() -> game.start());
        System.out.println("DEBUG: End cannotStartGameWithFewerThanMinPlayers");
    }

    /**
     * Test that players cannot be added after the game has started.
     */
    @Test
    void cannotAddPlayersAfterGameStart() {
        System.out.println("DEBUG: Start cannotAddPlayersAfterGameStart");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        assertThrows(IllegalStateException.class, () -> game.addPlayer("Player 3"));
        System.out.println("DEBUG: End cannotAddPlayersAfterGameStart");
    }

    /**
     * Test that player rotation works as expected after each frame.
     */
    @Test
    void playerRotationAfterEachFrame() {
        System.out.println("DEBUG: Start playerRotationAfterEachFrame");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        assertEquals("Player 1", game.getCurrentPlayer().getName());
        // Player 1 completes a frame (spare)
        game.addThrow(7);
        game.addThrow(8);
        assertEquals("Player 2", game.getCurrentPlayer().getName());
        // Player 2 completes a frame (open)
        game.addThrow(5);
        game.addThrow(5);
        game.addThrow(3);
        assertEquals("Player 1", game.getCurrentPlayer().getName());
        System.out.println("DEBUG: End playerRotationAfterEachFrame");
    }

    /**
     * End-to-end test: Simulate a complete game for two players, alternating throws as the game expects.
     * Each player plays 5 frames, each frame: 5, 5, 3 pins knocked down.
     */
    @Test
    void completeGameFlowWithTwoPlayers() {
        System.out.println("DEBUG: Start completeGameFlowWithTwoPlayers");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        // Simulate a complete game: alternate throws for each player as the game expects
        int[] throwsSequence = {
                5, 5, 3, 5, 5, 3,  // Frame 1 Player 1 & 2
                5, 5, 3, 5, 5, 3,  // Player 2, Frame 2
                5, 5, 3,  // Player 1, Frame 3
                5, 5, 3,  // Player 2, Frame 3
                5, 5, 3,  // Player 1, Frame 4
                5, 5, 3,  // Player 2, Frame 4
                5, 5, 3,  // Player 1, Frame 5
                5, 5, 3,  // Player 2, Frame 5
        };
        for (int pins : throwsSequence) {
            if (game.isGameComplete()) {
                System.out.println("Game is complete, breaking loop.");
                break;
            }
            try {
                game.addThrow(pins);
            } catch (Exception e) {
                e.printStackTrace();
                break; // Stop the loop if an exception occurs
            }
        }
        System.err.println("P1 and P2 have completed 5 frames");
        assertTrue(game.isGameComplete());
        // Check final scores
        var scoreboard = game.getScoreboard();
        assertEquals(2, scoreboard.size());
        assertEquals("Player 1", scoreboard.get(0).name());
        assertEquals("Player 2", scoreboard.get(1).name());
        assertEquals(scoreboard.get(0).score(), scoreboard.get(1).score());
        System.out.println("DEBUG: End completeGameFlowWithTwoPlayers");
    }

    /**
     * Test that bonus throws are handled correctly for the last frame (strike and spare).
     */
    @Test
    void bonusThrowsForLastFrame() {
        System.out.println("DEBUG: Start bonusThrowsForLastFrame");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        // Complete 4 frames for both players
        int[] throwsSequence = {
                5, 5, 3, 5, 5, 3, // Frame 1
                5, 5, 3, 5, 5, 3, // Frame 2
                5, 5, 3, 5, 5, 3, // Frame 3
                5, 5, 3, 5, 5, 3  // Frame 4
        };
        for (int pins : throwsSequence) {
            game.addThrow(pins);
        }
        // Player 1 gets a strike in last frame
        game.addThrow(15);
        // Player 2 completes last frame normally
        game.addThrow(5);
        game.addThrow(5);
        game.addThrow(3);
        // Player 1 should get bonus throws
        assertFalse(game.isGameComplete());
        //assertTrue(game.getCurrentPlayer().getCurrentFrame().isStrike());
        // Complete bonus throws for Player 1
        game.addThrow(10);
        game.addThrow(5);
        System.out.println(game.getCurrentPlayer().needsBonusThrows());
        game.addThrow(3);
        assertTrue(game.isGameComplete());
        System.out.println("DEBUG: End bonusThrowsForLastFrame");
    }

    /**
     * Test that throws cannot be made before the game starts.
     */
    @Test
    void cannotThrowBeforeGameStarts() {
        System.out.println("DEBUG: Start cannotThrowBeforeGameStarts");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        assertThrows(IllegalStateException.class, () -> game.addThrow(5));
        System.out.println("DEBUG: End cannotThrowBeforeGameStarts");
    }

    /**
     * Test the score calculation for a specific score chart example (see bowling score example).
     */
    @Test
    void scoreChartExample1() {
        System.out.println("DEBUG: Start scoreChartExample1");
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
        System.out.println("DEBUG: End scoreChartExample1");
    }
} 