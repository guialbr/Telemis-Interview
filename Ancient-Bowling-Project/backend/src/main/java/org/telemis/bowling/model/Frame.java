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
    // private boolean isLastFrame; //Not used
    
    public Frame() {
        this.throwList = new ArrayList<>();
        this.isCompleted = false;
    }
    
    public void addThrow(int pins) {
        if (isCompleted || throwList.size() >= MAX_THROWS) {
            throw new IllegalStateException("Frame is finished, cannot add more throws");
        }
        if (pins < 0 || pins > MAX_PINS) {
            throw new IllegalArgumentException("Invalid number of pins : " + pins);
        }
        if (!throwList.isEmpty() && getPinsKnockedDown() + pins > MAX_PINS) {
            throw new IllegalArgumentException("Total pins cannot exceed " + MAX_PINS);
        }
        
        throwList.add(pins);
        updateFrameStatus();
    }
    
    private void updateFrameStatus() {
        if (isStrike() || getPinsKnockedDown() == MAX_PINS || throwList.size() == MAX_THROWS) {
            isCompleted = true;
        }
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

    //CHECK FOR BONUS THROW IMPLEMENTATION ON LAST FRAME
    public List<Integer> getThrows() {
        return new ArrayList<>(throwList);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int getRemainingPins() {
        return MAX_PINS - getPinsKnockedDown();
    }
}
