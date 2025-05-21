package org.telemis.bowling.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class FrameTest {
    private Frame frame;

    @BeforeEach
    void setUp() {
        System.out.println("DEBUG: FrameTest.setUp");
        frame = new Frame();
    }

    @Test
    void testNewFrameIsNotComplete() {
        System.out.println("DEBUG: Start testNewFrameIsNotComplete");
        assertFalse(frame.isCompleted());
        System.out.println("DEBUG: End testNewFrameIsNotComplete");
    }

    @Test
    void testStrikeCompletesFrame() {
        System.out.println("DEBUG: Start testStrikeCompletesFrame");
        frame.addThrow(15);
        assertTrue(frame.isCompleted());
        assertTrue(frame.isStrike());
        assertFalse(frame.isSpare());
        System.out.println("DEBUG: End testStrikeCompletesFrame");
    }

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

    @Test
    void testCannotThrowAfterFrameComplete() {
        System.out.println("DEBUG: Start testCannotThrowAfterFrameComplete");
        frame.addThrow(15); // Strike
        assertThrows(IllegalStateException.class, () -> frame.addThrow(5));
        System.out.println("DEBUG: End testCannotThrowAfterFrameComplete");
    }

    @Test
    void testInvalidPinsThrown() {
        System.out.println("DEBUG: Start testInvalidPinsThrown");
        assertThrows(IllegalArgumentException.class, () -> frame.addThrow(-1));
        assertThrows(IllegalArgumentException.class, () -> frame.addThrow(16));
        System.out.println("DEBUG: End testInvalidPinsThrown");
    }

    @Test
    void testCannotExceedMaxPins() {
        System.out.println("DEBUG: Start testCannotExceedMaxPins");
        frame.addThrow(10);
        assertThrows(IllegalArgumentException.class, () -> frame.addThrow(6));
        System.out.println("DEBUG: End testCannotExceedMaxPins");
    }

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
        // Total score should be 60 (15 + 15 + 15)
        assertEquals(60, lastFrame.getPinsKnockedDown());
    }

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
    void testLastFrameInvalidThrows() {
        Frame lastFrame = new Frame(true);
        // First throw - Strike
        lastFrame.addThrow(15);
        
        // Second throw - should be limited by remaining pins
        assertThrows(IllegalArgumentException.class, () -> lastFrame.addThrow(16));
        
        // Valid second throw
        lastFrame.addThrow(10);
        
        // Third throw - should be limited by remaining pins
        assertThrows(IllegalArgumentException.class, () -> lastFrame.addThrow(6));
        
        // Valid third throw
        lastFrame.addThrow(5);
        
        // Fourth throw - should be limited by remaining pins
        assertThrows(IllegalArgumentException.class, () -> lastFrame.addThrow(11));
        
        // Valid fourth throw
        lastFrame.addThrow(10);
        assertTrue(lastFrame.isCompleted());
    }

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
        assertEquals(10, lastFrame.getRemainingPins()); // 15 - 5 = 10 remaining
        
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
} 