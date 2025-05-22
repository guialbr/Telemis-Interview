package org.telemis.bowling.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test suite for the Frame class in the Ancient African Bowling Game.
 * Tests all frame scenarios including regular frames and the special last frame.
 * Verifies scoring rules for:
 * - Regular throws (up to 14 points)
 * - Spares (15 points + 2 bonus throws)
 * - Strikes (15 points + 3 bonus throws)
 */
class FrameTest {
    private Frame frame;

    @BeforeEach
    void setUp() {
        System.out.println("DEBUG: FrameTest.setUp");
        frame = new Frame();
    }

    /**
     * Tests that a newly created frame is not marked as complete.
     */
    @Test
    void testNewFrameIsNotComplete() {
        System.out.println("DEBUG: Start testNewFrameIsNotComplete");
        assertFalse(frame.isCompleted());
        System.out.println("DEBUG: End testNewFrameIsNotComplete");
    }

    /**
     * Verifies that throws with invalid pin counts (-1 or 16) are rejected.
     */
    @Test
    void testInvalidPinsThrown() {
        System.out.println("DEBUG: Start testInvalidPinsThrown");
        assertThrows(IllegalArgumentException.class, () -> frame.addThrow(-1));
        assertThrows(IllegalArgumentException.class, () -> frame.addThrow(16));
        System.out.println("DEBUG: End testInvalidPinsThrown");
    }

    /**
     * Verifies that throws cannot exceed the maximum pin count (15) in a frame.
     */
    @Test
    void testCannotExceedMaxPins() {
        System.out.println("DEBUG: Start testCannotExceedMaxPins");
        frame.addThrow(10);
        assertThrows(IllegalArgumentException.class, () -> frame.addThrow(6));
        System.out.println("DEBUG: End testCannotExceedMaxPins");
    }

    /**
     * Tests that a strike (15 pins in first throw) properly completes the frame.
     */
    @Test
    void testStrikeCompletesFrame() {
        System.out.println("DEBUG: Start testStrikeCompletesFrame");
        frame.addThrow(15);
        assertTrue(frame.isCompleted());
        assertTrue(frame.isStrike());
        assertFalse(frame.isSpare());
        System.out.println("DEBUG: End testStrikeCompletesFrame");
    }

    /**
     * Tests that a spare (all 15 pins in two throws) properly completes the frame.
     */
    @Test
    void testSpareCompletesFrame() {
        System.out.println("DEBUG: Start testSpareCompletesFrame");
        frame.addThrow(7);
        frame.addThrow(8);
        assertTrue(frame.isCompleted());
        assertTrue(frame.isSpare());
        assertFalse(frame.isStrike());
        System.out.println("DEBUG: End testSpareCompletesFrame");
    }

    /**
     * Verifies that three throws complete a frame regardless of pins knocked down.
     */
    @Test
    void testThreeThrowsCompletesFrame() {
        System.out.println("DEBUG: Start testThreeThrowsCompletesFrame");
        frame.addThrow(5);
        frame.addThrow(5);
        frame.addThrow(4);
        assertTrue(frame.isCompleted());
        assertEquals(14, frame.getPinsKnockedDown());
        System.out.println("DEBUG: End testThreeThrowsCompletesFrame");
    }

    /**
     * Tests that additional throws are not allowed after frame completion.
     */
    @Test
    void testCannotThrowAfterFrameComplete() {
        System.out.println("DEBUG: Start testCannotThrowAfterFrameComplete");
        frame.addThrow(15); // Strike
        assertThrows(IllegalStateException.class, () -> frame.addThrow(5));
        System.out.println("DEBUG: End testCannotThrowAfterFrameComplete");
    }

    /**
     * Verifies that getThrows() returns a defensive copy of the throw list.
     */
    @Test
    void testGetThrowsReturnsCopy() {
        System.out.println("DEBUG: Start testGetThrowsReturnsCopy");
        frame.addThrow(5);
        frame.addThrow(7);
        List<Integer> throwList = frame.getThrows();
        assertEquals(2, throwList.size());
        throwList.add(3); // Should not affect the frame's internal state
        assertEquals(2, frame.getThrows().size());
        System.out.println("DEBUG: End testGetThrowsReturnsCopy");
    }

    /**
     * Tests the last frame behavior with a strike:
     * - Allows 3 bonus throws
     * - Verifies maximum score of 60 points
     * - Checks completion rules
     */
    @Test
    void testLastFrameStrike() {
        Frame lastFrame = new Frame(true);
        // First throw - Strike
        lastFrame.addThrow(15);
        assertTrue(lastFrame.isStrike());
        assertFalse(lastFrame.isCompleted());
        assertEquals(15, lastFrame.getRemainingPins());

        // First bonus throw
        lastFrame.addThrow(15); // Strike
        lastFrame.addThrow(15); // Second bonus throw
        lastFrame.addThrow(15); // Third bonus throw
        assertTrue(lastFrame.isCompleted());
        // Total score should be 60 : 15 +(15 + 15 + 15)
        assertEquals(60, lastFrame.getPinsKnockedDown());
    }


    @Test
    void testLastFrameStrikeAndSpare() {
        Frame lastFrame = new Frame(true);
        // First throw - Strike
        lastFrame.addThrow(15);
        assertTrue(lastFrame.isStrike());
        assertFalse(lastFrame.isCompleted());
        assertEquals(15, lastFrame.getRemainingPins());//Testing reset pins

        // First bonus throw
        lastFrame.addThrow(7); // Strike
        lastFrame.addThrow(8); // Second bonus throw
        assertEquals(15, lastFrame.getRemainingPins());//Testing reset pins after spare

        lastFrame.addThrow(15); // Third bonus throw
        assertTrue(lastFrame.isCompleted());
        // Total score should be 45 : 15 +(8 + 7 + 15)
        assertEquals(45, lastFrame.getPinsKnockedDown());
    }


    /**
     * Tests the last frame behavior with a spare in first two throws:
     * - Allows 2 bonus throws
     * - Verifies maximum score of 45 points
     * - Checks completion rules
     */
    @Test
    void testLastFrameSpareOnFirstTwoThrows() {
        Frame lastFrame = new Frame(true);
        // First throw
        lastFrame.addThrow(7);
        assertFalse(lastFrame.isSpare());
        assertFalse(lastFrame.isCompleted());
        assertEquals(8, lastFrame.getRemainingPins());

        // Second throw - makes spare
        lastFrame.addThrow(8);
        assertTrue(lastFrame.isSpare());
        assertFalse(lastFrame.isCompleted());
        assertEquals(15, lastFrame.getRemainingPins());

        // First bonus throw - Strike
        lastFrame.addThrow(15);
        assertFalse(lastFrame.isCompleted());
        assertEquals(15, lastFrame.getRemainingPins());

        // Second bonus throw - Strike
        lastFrame.addThrow(15);
        assertTrue(lastFrame.isCompleted());
        // Total score should be 45 (15 + 15 + 15)
        assertEquals(45, lastFrame.getPinsKnockedDown());
    }

    /**
     * Tests the last frame behavior with a spare in three throws:
     * - Allows 2 bonus throws
     * - Verifies pin counting
     * - Checks completion rules
     */
    @Test
    void testLastFrameSpareOnThreeThrows() {
        Frame lastFrame = new Frame(true);
        // First throw
        lastFrame.addThrow(5);
        assertFalse(lastFrame.isSpare());
        assertFalse(lastFrame.isCompleted());
        assertEquals(10, lastFrame.getRemainingPins());

        // Second throw
        lastFrame.addThrow(5);
        assertFalse(lastFrame.isSpare());
        assertFalse(lastFrame.isCompleted());
        assertEquals(5, lastFrame.getRemainingPins());

        // Third throw - makes spare
        lastFrame.addThrow(5);
        assertTrue(lastFrame.isSpare());
        assertFalse(lastFrame.isCompleted());
        assertEquals(15, lastFrame.getRemainingPins());

        // First bonus throw
        lastFrame.addThrow(10);
        assertFalse(lastFrame.isCompleted());
        assertEquals(5, lastFrame.getRemainingPins());

        // Second bonus throw
        lastFrame.addThrow(5);
        assertTrue(lastFrame.isCompleted());
    }

    /**
     * Tests regular last frame behavior without strike or spare:
     * - Completes after three throws
     * - Verifies maximum score of 14 points
     */
    @Test
    void testLastFrameRegular() {
        Frame lastFrame = new Frame(true);
        // First throw
        lastFrame.addThrow(5);
        assertFalse(lastFrame.isCompleted());
        assertEquals(10, lastFrame.getRemainingPins());

        // Second throw
        lastFrame.addThrow(5); 
        assertFalse(lastFrame.isCompleted());
        assertEquals(5, lastFrame.getRemainingPins());

        // Third throw
        lastFrame.addThrow(4);
        assertTrue(lastFrame.isCompleted());
        // Total score should be 14 (5 + 5 + 4)
        assertEquals(14, lastFrame.getPinsKnockedDown());
    }

    @Test
    void testLastFrameStrikeAndRegular() {
        Frame lastFrame = new Frame(true);
        // First throw
        lastFrame.addThrow(15);
        assertEquals(15, lastFrame.getRemainingPins());

        // Second throw
        lastFrame.addThrow(5);
        assertEquals(10, lastFrame.getRemainingPins());

        // Third throw
        lastFrame.addThrow(4);
        assertFalse(lastFrame.isCompleted());
        assertEquals(6, lastFrame.getPinsKnockedDown());

        // 4th throw / Last frame strike Bonus
        lastFrame.addThrow(6);
        // Total score should be 15 + (5 + 4 + 6) = 30
        assertTrue(lastFrame.isCompleted());
    }

    /**
     * Tests validation of throws in the last frame:
     * - Verifies pin limits after strikes
     * - Checks invalid throw rejection
     * - Ensures proper completion
     */
    @Test
    void testLastFrameInvalidThrows() {
        Frame lastFrame = new Frame(true);
        // First throw - Strike
        lastFrame.addThrow(15);
        assertTrue(lastFrame.isStrike());
        assertEquals(15, lastFrame.getRemainingPins()); //Pins are reset after last frame strike
        // Second throw - should be limited by remaining pins
        assertThrows(IllegalArgumentException.class, () -> lastFrame.addThrow(16));

        // Valid second throw & 1st bonus strike
        lastFrame.addThrow(10);
        assertEquals(5, lastFrame.getRemainingPins());
        // Third throw - should be limited by remaining pins
        assertThrows(IllegalArgumentException.class, () -> lastFrame.addThrow(6));

        // Valid third throw & 2nd Strike bonus (Spare)
        lastFrame.addThrow(5);
        assertEquals(15, lastFrame.getRemainingPins());

        // Valid fourth throw & 3rd/last strike bonus
        lastFrame.addThrow(10);
        assertTrue(lastFrame.isCompleted());
    }

    /**
     * Tests pin counting in various last frame scenarios:
     * - Strike followed by bonus throws (max 60)
     * - Spare followed by bonus throws (max 45)
     * - Regular throws (max 14)
     */
    @Test
    void testLastFramePinsKnockedDown() {
        Frame lastFrame = new Frame(true);
        // Strike + 3 bonus throws (all strikes)
        lastFrame.addThrow(15);
        lastFrame.addThrow(15);
        lastFrame.addThrow(15);
        lastFrame.addThrow(15);
        assertEquals(60, lastFrame.getPinsKnockedDown()); // Max possible for strike

        lastFrame = new Frame(true);
        // Spare on first two + 2 bonus throws (both strikes)
        lastFrame.addThrow(7);
        lastFrame.addThrow(8);
        lastFrame.addThrow(15);
        lastFrame.addThrow(15);
        assertEquals(45, lastFrame.getPinsKnockedDown()); // Max possible for spare

        lastFrame = new Frame(true);
        // Regular frame
        lastFrame.addThrow(5);
        lastFrame.addThrow(5);
        lastFrame.addThrow(4);
        assertEquals(14, lastFrame.getPinsKnockedDown()); // Max possible for regular
    }

    @Test
    void testPerfectGame() {
        // Create 5 frames for a perfect game
        Frame[] frames = new Frame[5];
        for (int i = 0; i < 5; i++) {
            frames[i] = new Frame(i == 4); // Last frame is special
        }

        // First 4 frames - all strikes
        for (int i = 0; i < 4; i++) {
            frames[i].addThrow(15);
            assertTrue(frames[i].isStrike());
            assertTrue(frames[i].isCompleted());
            assertEquals(15, frames[i].getPinsKnockedDown());
        }

        // Last frame - strike with 3 bonus throws
        frames[4].addThrow(15); // Strike
        frames[4].addThrow(15); // First bonus throw
        frames[4].addThrow(15); // Second bonus throw
        frames[4].addThrow(15); // Third bonus throw
        assertTrue(frames[4].isCompleted());
        assertEquals(60, frames[4].getPinsKnockedDown());

        // Calculate total score
        int totalScore = 0;
        for (Frame frame : frames) {
            totalScore += frame.getPinsKnockedDown();
        }
        assertEquals(300, totalScore); // Perfect game score
    }

    @Test
    void testLastFrameAllScenarios() {
        // Test 1: Strike followed by non-strike throws
        Frame lastFrame = new Frame(true);
        lastFrame.addThrow(15); // Strike
        assertEquals(15, lastFrame.getRemainingPins()); // Full set for first bonus throw
        
        lastFrame.addThrow(7); // First bonus throw
        assertEquals(8, lastFrame.getRemainingPins()); // 15 - 7 = 8 remaining
        
        lastFrame.addThrow(5); // Second bonus throw
        assertEquals(10, lastFrame.getRemainingPins()); // 8 - 5 = 3 remaining
        
        lastFrame.addThrow(3); // Third bonus throw
        assertTrue(lastFrame.isCompleted());
        assertEquals(30, lastFrame.getPinsKnockedDown()); // 15 + 7 + 5 + 3

        // Test 2: Spare followed by non-strike throws
        lastFrame = new Frame(true);
        lastFrame.addThrow(7);
        lastFrame.addThrow(8); // Spare
        assertEquals(15, lastFrame.getRemainingPins()); // Full set for first bonus throw
        
        lastFrame.addThrow(10); // First bonus throw
        assertEquals(5, lastFrame.getRemainingPins()); // 15 - 10 = 5 remaining
        
        lastFrame.addThrow(3); // Second bonus throw
        assertTrue(lastFrame.isCompleted());
        assertEquals(33, lastFrame.getPinsKnockedDown()); // 15 + 10 + 3

        // Test 3: Regular frame with three throws
        lastFrame = new Frame(true);
        lastFrame.addThrow(5);
        assertEquals(10, lastFrame.getRemainingPins()); // 15 - 5 = 10
        
        lastFrame.addThrow(5);
        assertEquals(5, lastFrame.getRemainingPins()); // 15 - 5 - 5 = 5
        
        lastFrame.addThrow(3);
        assertTrue(lastFrame.isCompleted());
        assertEquals(13, lastFrame.getPinsKnockedDown()); // 5 + 5 + 3
    }

    @Test
    void testLastFrameSpareAllowsBonusThrows() {
        Frame lastFrame = new Frame(true);
        // First two throws make a spare
        lastFrame.addThrow(7);
        lastFrame.addThrow(8);
        assertTrue(lastFrame.isSpare());
        assertFalse(lastFrame.isCompleted()); // Should not be completed yet
        
        // First bonus throw
        lastFrame.addThrow(10);
        assertFalse(lastFrame.isCompleted()); // Still not completed
        
        // Second bonus throw
        lastFrame.addThrow(5);
        assertTrue(lastFrame.isCompleted()); // Now completed
        assertEquals(30, lastFrame.getPinsKnockedDown()); // 15 + 10 + 5
    }
} 