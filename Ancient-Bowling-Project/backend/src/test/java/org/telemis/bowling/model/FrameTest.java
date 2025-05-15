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
} 