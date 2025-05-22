package org.telemis.bowling.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single frame in the ancient bowling game
 * Each frame consist of MAX_THROWS (3) throws to kick off the MAX_PINS (15) placed pins unless a strike is made in the first throw
 * Once completed, all 15 pins are reset
 **/

public class Frame {
    private static final int MAX_PINS = 15;
    private static final int MAX_THROWS = 3;

    private final List<Integer> throwList;
    private boolean isCompleted;
    private final boolean isLastFrame;
    private boolean isStrike;
    private boolean isSpare;

    /**
     * Creates a new regular frame with default settings.
     * Initializes an empty throw list and sets completion status to false.
     */
    public Frame() {
        this.throwList = new ArrayList<>(3);
        this.isCompleted = false;
        this.isLastFrame = false;
        this.isStrike = false;
        this.isSpare = false;
    }

    /**
     * Creates a new frame with specified last frame status.
     *
     * @param isLastFrame true if this is the last frame (5th frame), false otherwise
     */
    public Frame(boolean isLastFrame) {
        this.throwList = new ArrayList<>();
        this.isCompleted = false;
        this.isLastFrame = isLastFrame;
        this.isStrike = false;
        this.isSpare = false;
    }

    /**
     * Adds a throw to the current frame.
     * Validates the throw and updates frame completion status.
     *
     * @param pins number of pins knocked down in this throw
     * @throws IllegalStateException    if trying to add throws to a completed frame
     * @throws IllegalArgumentException if pins value is invalid (negative or greater than MAX_PINS)
     * @throws IllegalArgumentException if pins value exceeds remaining pins for this throw
     */
    public void addThrow(int pins) {
        if (isCompleted && !isLastFrame) {
            throw new IllegalStateException("Frame is finished, cannot add more throws");
        }
        if (pins < 0 || pins > MAX_PINS) {
            throw new IllegalArgumentException("Invalid number of pins : " + pins);
        }
        if (pins > getRemainingPins()) {
            throw new IllegalArgumentException("Cannot knock down more than the remaining pins: " + getRemainingPins());
        }
        
        throwList.add(pins);
        updateFrameStatus();
    }

    /**
     * Updates the frame's completion status based on throws made.
     * <p>
     * Regular frames (1-4) complete after:
     * - A strike (first throw)
     * - A spare (two/three throws)
     * - Three throws total
     * <p>
     * Last frame (5) requires:
     * - 3 bonus throws after strike
     * - 2 bonus throws after spare
     * - 3 throws otherwise
     */
    private void updateFrameStatus() {
        isStrike = isStrikeAt(0);
        isSpare = isSpareInTwoThrows() || isSpareInThreeThrows();

        if (!isLastFrame) {
            // Regular frame completes on strike, spare, or 3 throws
            if (isStrike || isSpare || throwList.size() == MAX_THROWS) {
                isCompleted = true;
            }
        } else {
            // Last frame has special rules for bonus throws
            if (isStrike) {
                // Strike in first throw of last frame needs total of 4 throws
                isCompleted = throwList.size() >= MAX_THROWS + 1; // 1 strike + 3 bonus throws
            } else if (isSpareInTwoThrows()) {
                // Spare in first two throws needs total of 4 throws
                isCompleted = throwList.size() >= MAX_THROWS + 1; // 2 throws for spare + 2 bonus throws
            } else if (isSpareInThreeThrows()) {
                // Spare in three throws needs total of 5 throws
                isCompleted = throwList.size() >= MAX_THROWS + 2; // 3 throws for spare + 2 bonus throws
            } else {
                // Regular last frame completes after 3 throws
                isCompleted = throwList.size() >= MAX_THROWS;
            }
        }
    }

    /**
     * Calculates the number of remaining pins for the next throw.
     * For regular frames: returns remaining pins from the current set.
     * For the last frame: handles all special cases for strikes and spares,
     * TODO Handle successive strikes on the last frame
     * including the possibility of up to 5 throws when a spare occurs on the third throw.
     *
     * @return the number of pins remaining for the next throw
     */
    public int getRemainingPins() {
        // For regular frames
        if (!isLastFrame) {
            return MAX_PINS - getPinsKnockedDown();
        }

        int currentThrow = throwList.size();
        // After a strike
        if (currentThrow >= 1 && isStrike) {
            if (currentThrow == 1) {
                // Reset pins after first strike
                return MAX_PINS;
            } else if (currentThrow == 2) {
                // Reset pins if second throw was also a strike
                if (isStrikeAt(1)) {
                    return MAX_PINS;
                }
                // Otherwise, calculate remaining from second throw
                return MAX_PINS - throwList.get(1);
            } else if (currentThrow == 3) {
                // For third throw after strike
                if (isStrikeAt(1) || // Second throw was strike
                        isStrikeAt(2) || // Third throw was strike
                        isSpareAt(1)) {         // Second and third throws form a spare
                    return MAX_PINS;
                }
                // Otherwise, calculate remaining from second throw
                return MAX_PINS - (throwList.get(1) + throwList.get(2));
            }
        }
        // After a spare (in first two throws)
        if (currentThrow >= 2 && isSpareAt(0) && !isStrike) {
            if (currentThrow == 2) {
                return MAX_PINS; // Reset pins after a spare
            } else if (currentThrow == 3) {
                // For first bonus throw
                if (isStrikeAt(2)) {
                    return MAX_PINS; // Reset pins after strike
                } else {
                    return MAX_PINS - throwList.get(2);
                }
            } else {
                // For second bonus throw
                return MAX_PINS - (throwList.get(2) + throwList.get(3));
            }
        }
        // After a spare (in three throws)
        if (currentThrow >= 3 &&
                isSpareInThreeThrows() &&
                !isStrike() &&
                !isSpareInTwoThrows()) {
            if (currentThrow == 3) {
                return MAX_PINS; // Reset pins after a spare
            } else if (currentThrow == 4) {
                // For first bonus throw
                if (isStrikeAt(3)) {
                    return MAX_PINS; // Reset pins if it is a strike
                } else {
                    return MAX_PINS - throwList.get(3);
                }
            } else {
                // For second bonus throw
                return MAX_PINS - (throwList.get(3) + throwList.get(4));
            }
        }
        // Normal calculation for other cases
        int currentFramePins = 0;
        for (int i = 0; i < currentThrow && i < 3; i++) {
            currentFramePins += throwList.get(i);
        }
        return MAX_PINS - currentFramePins;
    }

    /**
     * Checks if the current frame starts with a strike.
     * A strike occurs when all pins are knocked down in the first throw.
     *
     * @return true if the frame starts with a strike, false otherwise
     */
    public boolean isStrike() {
        return isStrike;
    }

    /**
     * Checks if the throw at a given index is a strike (all pins knocked down).
     *
     * @param index the index of the throw to check
     * @return true if the throw at the given index is a strike, false otherwise
     */
    private boolean isStrikeAt(int index) {
        return throwList.size() >= index && throwList.get(index) == MAX_PINS;
    }

    /**
     * Determines if the current frame is a spare.
     * A spare occurs when all pins are knocked down in either:
     * - Two throws (not a strike)
     * - Three throws (not a strike and first two throws didn't knock down all pins)
     *
     * @return true if the frame is a spare, false otherwise
     */
    public boolean isSpare() {
        return isSpare;
    }

    /**
     * Checks if the throw at a given index is a spare .
     * Suitable for last frame as it ignore strike flag
     * spare checking after a strike.
     *
     * @param index the starting index to check for spare
     * @return true if the throws at startIndex and startIndex+1 form a spare
     */
    private boolean isSpareAt(int index) {
        return throwList.size() >= index + 2 &&
                throwList.get(index) + throwList.get(index + 1) == MAX_PINS;
    }

    private boolean isSpareInTwoThrows() {
        return throwList.size() >= 2 &&
                throwList.get(0) + throwList.get(1) == MAX_PINS &&
                !isStrike();
    }

    private boolean isSpareInThreeThrows() {
        return throwList.size() >= 3 &&
                throwList.get(0) + throwList.get(1) + throwList.get(2) == MAX_PINS &&
                throwList.get(0) != MAX_PINS &&
                throwList.get(0) + throwList.get(1) != MAX_PINS;
    }

    /**
     * Calculates the total number of pins knocked down in the current frame.
     *
     * @return sum of all throws in the current frame
     */
    public int getPinsKnockedDown() {
        return throwList.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Returns a copy of the list of throws in the current frame.
     *
     * @return new ArrayList containing all throws in the current frame
     */
    public List<Integer> getThrows() {
        return new ArrayList<>(throwList);
    }

    /**
     * Checks if the current frame is completed.
     * A frame is completed when:
     * - Regular frame: after a strike, spare, or 3 throws
     * - Last frame: after all required throws including bonus throws
     *
     * @return true if the frame is completed, false otherwise
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Checks if this is the last frame (5th frame) of the game.
     * The last frame has special rules for bonus throws.
     *
     * @return true if this is the last frame, false otherwise
     */
    public boolean isLastFrame() {
        return isLastFrame;
    }
}