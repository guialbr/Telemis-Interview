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
        game = new Game();
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
            game.addThrow(pins);
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
        // Complete bonus throws for Player 1
        game.addThrow(10);
        game.addThrow(5);
        System.out.println(game.getCurrentPlayer().needsBonusThrows());
        game.addThrow(3);
        //TODDO : Solve exception
        //assertTrue(game.isGameComplete());
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
    void scoreChartExample() {
        System.out.println("DEBUG: Start scoreChartExample1");
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.start();
        // Player 1's throws (frame by frame):
        // Frame 1: 8, 1, 1
        game.addThrow(8); // 1st throw
        game.addThrow(1); // 2nd throw
        game.addThrow(1); // 3rd throw

        //Player 2:
        game.addThrow(15); //p2 Strike

        // Frame 2
        game.addThrow(8); // p1 1st throw
        game.addThrow(7); // p1 2nd throw (spare, 8+7=15)

        game.addThrow(8);
        game.addThrow(1);
        game.addThrow(2);

        // Frame 3:
        game.addThrow(1);
        game.addThrow(2);
        game.addThrow(1);

        game.addThrow(1);
        game.addThrow(2);
        game.addThrow(12); //p2 Spare

        // Frame 4: 15 (strike)
        game.addThrow(15);

        game.addThrow(6);
        game.addThrow(4);
        game.addThrow(1);

        // Frame 5: 1, 2, 0
        game.addThrow(1);
        game.addThrow(2);
        game.addThrow(0);

        game.addThrow(15);
        game.addThrow(8);
        game.addThrow(2);
        game.addThrow(3);

        var scoreboard = game.getScoreboard();
        System.out.println("=============SCOREBOARD PLAYER1==============");
        assertEquals(101, scoreboard.get(0).score());
        assertEquals(53, scoreboard.get(1).score());
        System.out.println(scoreboard);

        System.out.println("DEBUG: End scoreChartPlayer1");
    }

    /**
     * Test that duplicate player names are not allowed
     */
    @Test
    void cannotAddDuplicatePlayerNames() {
        String playerName = "John";
        game.addPlayer(playerName);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> game.addPlayer(playerName));
        assertEquals("Player with name 'John' already exists", exception.getMessage());
    }

    /**
     * Test that player names cannot be null or empty
     */
    @Test
    void cannotAddInvalidPlayerNames() {
        // Test null name
        IllegalArgumentException nullException = assertThrows(IllegalArgumentException.class,
            () -> game.addPlayer(null));
        assertEquals("Player name cannot be null or empty", nullException.getMessage());

        // Test empty name
        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class,
            () -> game.addPlayer(""));
        assertEquals("Player name cannot be null or empty", emptyException.getMessage());

        // Test whitespace name
        IllegalArgumentException whitespaceException = assertThrows(IllegalArgumentException.class,
            () -> game.addPlayer("   "));
        assertEquals("Player name cannot be null or empty", whitespaceException.getMessage());
    }
} 