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
    
    public Frame() {
        this.throwList = new ArrayList<>(3);
        this.isCompleted = false;
        this.isLastFrame = false;
    }

    public Frame(boolean isLastFrame) {
        this.throwList = new ArrayList<>();
        this.isCompleted = false;
        this.isLastFrame = isLastFrame;
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

    public void updateFrameStatus() {
        if (isLastFrame()) {
            isCompleted = checkLastFrameCompletion();
        } else {
            isCompleted = checkRegularFrameCompletion();
        }
        if (isCompleted) {
            System.out.println("DEBUG: Frame completed. Throws: " + throwList + ", Total: " + getPinsKnockedDown());
        }
    }

    private boolean checkLastFrameCompletion() {
        int throwsCount = getThrows().size();

        return (isStrike() && throwsCount == MAX_THROWS + 1) ||
                (isSpareOnFirstTwoThrows() && throwsCount == MAX_THROWS + 1) ||
                (isSpareOnThreeThrows() && throwsCount == MAX_THROWS + 2) ||
                (isRegularFrame() && throwsCount == MAX_THROWS);
    }

    private boolean checkRegularFrameCompletion() {
        return isStrike() || getPinsKnockedDown() == MAX_PINS || getThrows().size() == MAX_THROWS;
    }


    private boolean isSpareOnFirstTwoThrows() {
        List<Integer> throws_ = getThrows();
        return throws_.size() >= 2 &&
                (throws_.get(0) + throws_.get(1) == MAX_PINS) &&
                !isStrike();
    }

    private boolean isSpareOnThreeThrows() {
        List<Integer> throws_ = getThrows();
        return throws_.size() >= 3 &&
                (throws_.get(0) + throws_.get(1) + throws_.get(2) == MAX_PINS) &&
                !isStrike();
    }

    private boolean isRegularFrame() {
        return !isStrike() && !isSpare();
    }

    public boolean isStrike() {
        return !throwList.isEmpty() && throwList.get(0) == MAX_PINS;
    }
    
    public boolean isSpare() {
        return !isStrike() && throwList.size() >= 2 && getPinsKnockedDown() == MAX_PINS;
    }

    public int getPinsKnockedDown   () {
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
        return MAX_PINS - getPinsKnockedDown();
    }
}
