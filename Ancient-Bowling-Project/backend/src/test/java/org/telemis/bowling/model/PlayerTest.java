package org.telemis.bowling.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        System.out.println("DEBUG: PlayerTest.setUp");
        player = new Player("Test Player");
    }

    @Test
    void testNewPlayerHasOneFrame() {
        System.out.println("DEBUG: Start testNewPlayerHasOneFrame");
        assertEquals(1, player.getFrames().size());
        assertFalse(player.isGameComplete());
        System.out.println("DEBUG: End testNewPlayerHasOneFrame");
    }

    @Test
    void testStrikeScore() {
        System.out.println("DEBUG: Start testStrikeScore");
        // First frame - Strike
        player.addThrow(15);
        player.addThrow(3);
        player.addThrow(4);
        player.addThrow(5 );
        assertEquals(27, player.calculateScore(1)); // should be: 15 + (3 + 4 + 5) = 27
        assertEquals(39, player.calculateScore(2)); // should be: scoreFrame1 + (3 + 4 + 5) = 39
        System.out.println("DEBUG: End testStrikeScore");
    }

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
        // Frame 5:  (6 + 4 + 3) = 13
        // Total: 109
        assertEquals(35, player.calculateScore(1));
        assertEquals(58, player.calculateScore(2));
        assertEquals(68, player.calculateScore(3));
        assertEquals(96, player.calculateScore(4));
        assertEquals(109, player.calculateScore(5));
        assertEquals(109, player.calculateScore());
        System.out.println("DEBUG: End testCompleteGame");
    }

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
} 