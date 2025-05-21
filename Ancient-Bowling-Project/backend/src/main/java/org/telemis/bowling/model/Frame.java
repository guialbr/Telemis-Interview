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
    private static final int BONUS_THROWS_AFTER_SPARE = 2;
    private static final int BONUS_THROWS_AFTER_STRIKE = 3;

    private final List<Integer> throwList;
    private boolean isCompleted;
    private final boolean isLastFrame;
    private int bonusThrowsCounter;  // Tracks number of bonus throws taken in last frame
    
    public Frame() {
        this.throwList = new ArrayList<>(3);
        this.isCompleted = false;
        this.isLastFrame = false;
        this.bonusThrowsCounter = 0;
    }

    public Frame(boolean isLastFrame) {
        this.throwList = new ArrayList<>();
        this.isCompleted = false;
        this.isLastFrame = isLastFrame;
        this.bonusThrowsCounter = 0;
    }

    public void addThrow(int pins) {
        if (isCompleted && !isLastFrame) {
            throw new IllegalStateException("Frame is finished, cannot add more throws");
        }
        if (pins < 0 || pins > MAX_PINS) {
            throw new IllegalArgumentException("Invalid number of pins : " + pins);
        }
        if (!throwList.isEmpty() && getPinsKnockedDown() + pins > MAX_PINS && !isLastFrame) {
            throw new IllegalArgumentException("Total pins cannot exceed " + MAX_PINS);
        }
        
        throwList.add(pins);
        updateFrameStatus();
    }

    private void updateFrameStatus() {
        if (!isLastFrame) {
            // Regular frame completes on strike, spare, or 3 throws
            if (isStrike() || isSpare() || throwList.size() == 3) {
                isCompleted = true;
            }
        } else {
            // Last frame has special rules for bonus throws
            if (isStrike()) {
                // Strike in last frame needs 3 bonus throws
                isCompleted = throwList.size() == BONUS_THROWS_AFTER_STRIKE + 1;
            } else if (isSpare()) {
                // For spare in last frame, we need exactly 2 bonus throws
                if (throwList.size() == 2) {
                    // Spare in first two throws
                    isCompleted = false;
                    bonusThrowsCounter = 0;
                } else if (throwList.size() == 3) {
                    // Spare in three throws
                    isCompleted = false;
                    bonusThrowsCounter = 0;
                } else if (throwList.size() > 3) {
                    // Count bonus throws (total throws minus the throws that made the spare)
                    bonusThrowsCounter = throwList.size() - (isFirstTwoThrowsSpare() ? 2 : 3);
                    // Only complete if we've taken exactly 2 bonus throws
                    isCompleted = bonusThrowsCounter == BONUS_THROWS_AFTER_SPARE;
                }
            } else {
                // Regular last frame completes after 3 throws
                isCompleted = throwList.size() == 3;
            }
        }
    }

    private boolean isFirstTwoThrowsSpare() {
        return throwList.size() >= 2 &&
                throwList.get(0) + throwList.get(1) == MAX_PINS &&
                !isStrike();
    }

    public boolean isStrike() {
        return !throwList.isEmpty() && throwList.get(0) == MAX_PINS;
    }
    
    public boolean isSpare() {
        if (throwList.size() >= 3) {
            // For three throws, check if first two throws made a spare
            if (throwList.get(0) + throwList.get(1) == MAX_PINS && !isStrike()) {
                return true;
            }
            // Check if all three throws made a spare
            return throwList.get(0) + throwList.get(1) + throwList.get(2) == MAX_PINS &&
                    !isStrike() &&
                    throwList.get(0) + throwList.get(1) != MAX_PINS;
        } else if (throwList.size() >= 2) {
            // Check for spare in two throws
            return throwList.get(0) + throwList.get(1) == MAX_PINS && !isStrike();
        }
        return false;
    }

    public int getPinsKnockedDown() {
        return throwList.stream().mapToInt(Integer::intValue).sum();
    }

    public List<Integer> getThrows() {
        return new ArrayList<>(throwList);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isLastFrame() {
        return isLastFrame;
    }

    public int getRemainingPins() {
        // For regular frames
        if (!isLastFrame) {
            return MAX_PINS - getPinsKnockedDown();
        }

        // For the last frame
        if (isLastFrame) {
            int currentThrow = throwList.size();

            // After a strike
            if (currentThrow >= 1 && throwList.get(0) == MAX_PINS) {
                if (currentThrow == 1) {
                    // Reset pins after first strike
                    return MAX_PINS;
                } else if (currentThrow == 2) {
                    // If second throw is also a strike, reset pins
                    if (throwList.get(1) == MAX_PINS) {
                        return MAX_PINS;
                    } else {
                        // Otherwise, calculate remaining pins
                        return MAX_PINS - throwList.get(1);
                    }
                } else if (currentThrow == 3) {
                    // Before last throw, reset pins if previous was strike
                    if (throwList.get(1) == MAX_PINS) {
                        return MAX_PINS;
                    } else {
                        // Continue with same frame if no strike in second throw
                        return MAX_PINS - throwList.get(2);
                    }
                }
            }

            // After a spare (in first two throws)
            if (currentThrow >= 2 && throwList.get(0) + throwList.get(1) == MAX_PINS && throwList.get(0) != MAX_PINS) {
                if (currentThrow == 2) {
                    return MAX_PINS; // Reset pins after a spare
                } else if (currentThrow == 3) {
                    // For first bonus throw
                    if (throwList.get(2) == MAX_PINS) {
                        return MAX_PINS; // Reset pins after strike
                    } else {
                        return MAX_PINS - throwList.get(2);
                    }
                } else {
                    // For second bonus throw
                    return MAX_PINS - throwList.get(3);
                }
            }

            // After a spare (in three throws)
            if (currentThrow >= 3 &&
                    throwList.get(0) + throwList.get(1) + throwList.get(2) == MAX_PINS &&
                    throwList.get(0) != MAX_PINS &&
                    throwList.get(0) + throwList.get(1) != MAX_PINS) {
                if (currentThrow == 3) {
                    return MAX_PINS; // Reset pins after a spare
                } else if (currentThrow == 4) {
                    // For first bonus throw
                    if (throwList.get(3) == MAX_PINS) {
                        return MAX_PINS; // Reset pins after strike
                    } else {
                        return MAX_PINS - throwList.get(3);
                    }
                } else {
                    // For second bonus throw
                    return MAX_PINS - throwList.get(4);
                }
            }

            // Normal calculation for other cases
            int currentFramePins = 0;
            for (int i = 0; i < currentThrow && i < 3; i++) {
                currentFramePins += throwList.get(i);
            }
            return MAX_PINS - currentFramePins;
        }

        return 0; // Default case
    }
}
