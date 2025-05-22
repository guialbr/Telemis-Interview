package org.telemis.bowling.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Player class in the Ancient African Bowling Game.
 * Tests player-specific functionality including:
 * - Frame management
 * - Score calculation
 * - Game completion rules
 * - Bonus throw handling
 */
class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        System.out.println("DEBUG: PlayerTest.setUp");
        player = new Player("Test Player");
    }

    /**
     * Tests that a new player starts with one frame and the game is not complete.
     * Verifies initial state setup for a new player.
     */
    @Test
    void testNewPlayerHasOneFrame() {
        System.out.println("DEBUG: Start testNewPlayerHasOneFrame");
        assertEquals(1, player.getFrames().size());
        assertFalse(player.isGameComplete());
        System.out.println("DEBUG: End testNewPlayerHasOneFrame");
    }

    /**
     * Tests strike scoring rules:
     * - Strike (15 pins) + next 3 throws
     * - Verifies correct score calculation for frame with strike
     * - Checks cumulative scoring across frames
     */
    @Test
    void testStrikeScore() {
        System.out.println("DEBUG: Start testStrikeScore");
        // First frame - Strike
        player.addThrow(15);
        player.addThrow(3);
        player.addThrow(4);
        player.addThrow(5);
        assertEquals(27, player.calculateScore(1)); // should be: 15 + (3 + 4 + 5) = 27
        assertEquals(39, player.calculateScore(2)); // should be: scoreFrame1 + (3 + 4 + 5) = 39
        System.out.println("DEBUG: End testStrikeScore");
    }

    /**
     * Tests spare scoring rules:
     * - Spare (15 pins in two throws) + next 2 throws
     * - Verifies correct score calculation for frame with spare
     * - Checks proper bonus throw counting
     */
    @Test
    void testSpareScore() {
        System.out.println("DEBUG: Start testSpareScore");
        // First frame - Spare
        player.addThrow(7);
        player.addThrow(8);
        // Second frame - 5, 3
        player.addThrow(5);
        player.addThrow(3);
        
        // Spare score should be: 15 + (5 + 3) + 8 = 31
        assertEquals(23, player.calculateScore(1)); // Should be: 15 + (5 + 3) = 23
        assertEquals(31, player.calculateScore(2));// Should be: scoreFrame1 + (5 + 3) = 31
        System.out.println("DEBUG: End testSpareScore");
    }

    /**
     * Tests a complete game with mixed throws:
     * - Strike, spare, open frame sequence
     * - Frame-by-frame score calculation
     * - Final score verification
     * - Game completion status
     * Score breakdown:
     * - Frame 1: Strike (15) + bonus (20) = 35
     * - Frame 2: Spare (15) + bonus (8) = 23
     * - Frame 3: Open frame = 10
     * - Frame 4: Strike (15) + bonus (13) = 28
     * - Frame 5: Open frame = 13
     * Total: 109 points
     */
    @Test
    void testCompleteGame() {
        System.out.println("DEBUG: Start testCompleteGame");
        // Frame 1 - Strike
        player.addThrow(15);
        // Frame 2 - Spare
        player.addThrow(7);
        player.addThrow(8);
        // Frame 3 - Open
        player.addThrow(5);
        assertEquals(35, player.calculateScore(1));
        player.addThrow(3);
        player.addThrow(2);
        // Frame 4 - Strike
        player.addThrow(15);
        // Frame 5 - Open
        player.addThrow(6);
        player.addThrow(4);
        player.addThrow(3);
        
        assertTrue(player.isGameComplete());
        // Score calculation:
        // Frame 1: 15 + (7 + 8 + 5) = 35
        // Frame 2: 15 + (5 + 3) = 23
        // Frame 3: 5 + 3 + 2 = 10
        // Frame 4: 15 + (6 + 4 + 3) = 28
        // Frame 5: (6 + 4 + 3) = 13
        // Total: 109
        assertEquals(35, player.calculateScore(1));
        assertEquals(58, player.calculateScore(2));
        assertEquals(68, player.calculateScore(3));
        assertEquals(96, player.calculateScore(4));
        assertEquals(109, player.calculateScore(5));
        assertEquals(109, player.calculateScore());
        System.out.println("DEBUG: End testCompleteGame");
    }

    /**
     * Tests bonus throws handling for a strike in the last frame:
     * - Regular frames with consistent scoring
     * - Strike in last frame
     * - Three bonus throws allowed
     * - Game completion verification
     */
    @Test
    void testBonusThrowsForLastFrameStrike() {
        System.out.println("DEBUG: Start testBonusThrowsForLastFrameStrike");
        // Frames 1-4
        for (int i = 0; i < 4; i++) {
            player.addThrow(5);
            player.addThrow(5);
            player.addThrow(4);
        }
        // Frame 5 - Strike
        player.addThrow(15);
        assertFalse(player.isGameComplete()); // Need bonus throws
        
        // Bonus throws
        player.addThrow(10);
        player.addThrow(5);
        player.addThrow(3);
        
        assertTrue(player.isGameComplete());
        System.out.println("DEBUG: End testBonusThrowsForLastFrameStrike");
    }

    /**
     * Tests bonus throws handling for a spare in the last frame:
     * - Regular frames with consistent scoring
     * - Spare in last frame
     * - Two bonus throws allowed
     * - Game completion verification
     */
    @Test
    void testBonusThrowsForLastFrameSpare() {
        System.out.println("DEBUG: Start testBonusThrowsForLastFrameSpare");
        // Frames 1-4
        for (int i = 0; i < 4; i++) {
            player.addThrow(5);
            player.addThrow(5);
            player.addThrow(4);
        }
        // Frame 5 - Spare
        player.addThrow(7);
        player.addThrow(8); //Spare on 2nd throw
        assertFalse(player.isGameComplete()); // Need bonus throws

        // Bonus throws
        player.addThrow(10);
        player.addThrow(5);
        
        assertTrue(player.isGameComplete());
        System.out.println("DEBUG: End testBonusThrowsForLastFrameSpare");
    }

    /**
     * Tests that throws are not allowed after game completion:
     * - Complete game with regular frames
     * - Verify game completion status
     * - Ensure additional throws are rejected
     */
    @Test
    void testCannotThrowAfterGameComplete() {
        System.out.println("DEBUG: Start testCannotThrowAfterGameComplete");
        // Complete a game without bonus frames
        for (int i = 0; i < 5; i++) {
            player.addThrow(5);
            player.addThrow(5);
            player.addThrow(4);
        }
        
        assertTrue(player.isGameComplete());
        assertThrows(IllegalStateException.class, () -> player.addThrow(5));
        System.out.println("DEBUG: End testCannotThrowAfterGameComplete");
    }

    /**
     * Tests a perfect game where all throws are strikes (300 points).
     * In the Ancient African Bowling game:
     * - Each frame has 15 pins
     * - A strike scores 15 + next 3 throws
     * - 5 frames total
     * - Last frame gets 3 bonus throws for strike
     * Perfect game calculation:
     * Frames 1-4: 4 × (15 + 45) = 240 points
     * Frame 5: 15 + (15 + 15 + 15) = 60 points
     * Total: 300 points
     */
    @Test
    void testPerfectGame() {
        System.out.println("DEBUG: Start testPerfectGame");
        
        // First 4 frames - all strikes
        for (int i = 0; i < 4; i++) {
            player.addThrow(15); // Strike
        }
        
        // Last frame - strike + 3 bonus throws
        player.addThrow(15); // Strike in last frame
        player.addThrow(15); // First bonus throw
        player.addThrow(15); // Second bonus throw
        player.addThrow(15); // Third bonus throw
        
        assertTrue(player.isGameComplete());
        
        // Verify frame-by-frame scores
        assertEquals(60, player.calculateScore(1));  // Frame 1: 15 + (15 + 15 + 15) = 60
        assertEquals(120, player.calculateScore(2)); // Frame 2: 60 + 60 = 120
        assertEquals(180, player.calculateScore(3)); // Frame 3: 120 + 60 = 180
        assertEquals(240, player.calculateScore(4)); // Frame 4: 180 + 60 = 240
        assertEquals(300, player.calculateScore(5)); // Frame 5: 240 + 60 = 300
        assertEquals(300, player.calculateScore());  // Total score
        
        System.out.println("DEBUG: End testPerfectGame");
    }

    /**
     * Tests Player 1's specific game sequence from the score chart:
     * Frame 1: 8,1,1 = 10 points
     * Frame 2: 8,7(spare) = 28 cumulative
     * Frame 3: 1,2,1 = 31 cumulative
     * Frame 4: Strike(X) = 50 cumulative
     * Frame 5: 1,2,1 = 53 final score
     */
    @Test
    void testPlayer1ScoreChart() {
        System.out.println("DEBUG: Start testPlayer1ScoreChart");

        // Frame 1: 8,1,1
        player.addThrow(8);
        player.addThrow(1);
        player.addThrow(1);

        // Frame 2: 8,7 (Spare) : 10 + 15 + 3 = 28
        player.addThrow(8);
        player.addThrow(7);

        // Frame 3: 1,2,1 : 28 + 4
        player.addThrow(1);
        player.addThrow(2);
        player.addThrow(1);

        // Frame 4: Strike (X) 32 + 15 + 1 + 2 + 0 = 50
        player.addThrow(15);

        // Frame 5: 1,2,1 51 + 3
        player.addThrow(1);
        player.addThrow(2);
        player.addThrow(0);

        assertTrue(player.isGameComplete());
        assertEquals(10, player.calculateScore(1));
        assertEquals(28, player.calculateScore(2)); // 1// 0 + (15 + 3)
        assertEquals(32, player.calculateScore(3));
        assertEquals(50, player.calculateScore(4)); // Previous 32 + (15 + 4)


        assertEquals(53, player.calculateScore());

        System.out.println("DEBUG: End testPlayer1ScoreChart");
    }

    /**
     * Tests Player 2's specific game sequence from the score chart:
     * Frame 1: Strike(X) = 26 points
     * Frame 2: 8,1,2 = 37 cumulative
     * Frame 3: 1,2,12(Spare) = 62 cumulative
     * Frame 4: 6,4,1 = 73 cumulative
     * Frame 5: Strike(X),8,2,3 = 101 final score
     */
    @Test
    void testPlayer2ScoreChart() {
        System.out.println("DEBUG: Start testPlayer2ScoreChart");

        // Frame 1: Strike (X)
        player.addThrow(15);

        // Frame 2: 8,1,2
        player.addThrow(8);
        player.addThrow(1);
        player.addThrow(2);

        // Frame 3: 1,2,12 (making a spare with last throw)
        player.addThrow(1);
        player.addThrow(2);
        player.addThrow(12);

        // Frame 4: 6,4,1
        player.addThrow(6);
        player.addThrow(4);
        player.addThrow(1);


        // Frame 5: Strike (X) + bonus throws 8,2,3
        player.addThrow(15); // Strike
        player.addThrow(8);
        player.addThrow(2);
        player.addThrow(3);

        assertTrue(player.isGameComplete());

        assertEquals(26, player.calculateScore(1)); // 15 + (8 + 1 + 2)
        assertEquals(37, player.calculateScore(2));
        assertEquals(62, player.calculateScore(3));
        assertEquals(73, player.calculateScore(4));

        assertEquals(101, player.calculateScore());

        System.out.println("DEBUG: End testPlayer2ScoreChart");
    }
}

