package org.telemis.bowling.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a single player in the game
 * Score calculation
 **/

public class Player {
    private static final int MAX_FRAMES = 5;
    private final String name;
    private final List<Frame> frames;
    private Frame currentFrame;
    
    public Player(String name) {
        this.name = name;
        this.frames = new ArrayList<>();
        this.currentFrame = new Frame();
        frames.add(currentFrame);
    }

    /**
     * Ensures a new frame is started if the previous one is completed.
     * Must be called before addThrow().
     * Only creates a new frame if not on the last frame; bonus throws for the last frame are added to the last frame.
     */
    public void ensureFreshFrame() {
        if (currentFrame.isCompleted() && frames.size() < MAX_FRAMES) {
            if (frames.size() == MAX_FRAMES - 1) {
                currentFrame = new Frame(true); 
            } else {
                currentFrame = new Frame();
            }
            frames.add(currentFrame);
        }
        
        // If on the last frame, do not create a new frame; bonus throws will be added to the last frame
    }

    /**
     * Adds a throw to the current frame.
     * Call ensureFreshFrame() before this method.
     * If on the last frame, all throws (including bonus) are added to the last frame.
     */
    public void addThrow(int pins) {
        ensureFreshFrame(); // Create a new frame if the previous one is completed
        if (isGameComplete()) {
            if (!needsBonusThrows()) {
                throw new IllegalStateException("Game is complete");
            }
        }
        // If on the last frame, always add throws to the last frame (including bonus throws)
        if (frames.size() == MAX_FRAMES) {
            frames.get(MAX_FRAMES - 1).addThrow(pins);
        } else {
            currentFrame.addThrow(pins);
        }
    }
    
    /**
     * Calculates the cumulative score up to the given frame (1-based index).
     * If frameNumber == frames.size(), returns the total score.
     */
    public int calculateScore() {
        return calculateScore(frames.size());
    }
    
    public int calculateScore(int frameNumber) {
        int totalScore = 0;
        for (int i = 0; i < frameNumber && i < frames.size(); i++) {
            Frame frame = frames.get(i);
            totalScore += frame.getPinsKnockedDown();
            if (i < MAX_FRAMES) {
                if (frame.isStrike()) {
                    totalScore += calculateStrikeBonus(i);
                } else if (frame.isSpare()) {
                    totalScore += calculateSpareBonus(i);
                }
            }
        }
        return totalScore;
    }
    
    private int calculateStrikeBonus(int frameIndex) {
        List<Integer> nextThrows = getNextThrows(frameIndex, 3);
        System.out.println("Debug: StrikeBonus calculated: " + nextThrows);
        return nextThrows.stream().mapToInt(Integer::intValue).sum();
    }
    
    private int calculateSpareBonus(int frameIndex) {
        List<Integer> nextThrows = getNextThrows(frameIndex, 2);
        System.out.println("Debug: SpareBonus calculated: " + nextThrows);
        return nextThrows.stream().mapToInt(Integer::intValue).sum();
    }
    
    private List<Integer> getNextThrows(int frameIndex, int count) {
        List<Integer> throwList = new ArrayList<>();
        int currentThrow = 0;
        int currentFrameIndex = frameIndex + 1;
        
        while (currentThrow < count && currentFrameIndex < frames.size()) {
            Frame frame = frames.get(currentFrameIndex);
            List<Integer> frameThrows = frame.getThrows();
            
            for (int pins : frameThrows) {
                if (currentThrow < count) {
                    throwList.add(pins);
                    currentThrow++;
                }
            }
            currentFrameIndex++;
        }
        
        return throwList;
    }
    
    public boolean isGameComplete() {
        boolean enoughFrames = frames.size() >= MAX_FRAMES;
        boolean lastCompleted = enoughFrames && frames.get(MAX_FRAMES - 1).isCompleted();
        boolean noBonus = !needsBonusThrows();
        boolean result = enoughFrames && lastCompleted && noBonus;
        System.out.println("DEBUG: Player " + name + " isGameComplete: " + result +
                           " (frames: " + frames.size() +
                           ", last completed: " + lastCompleted +
                           ", needsBonus: " + needsBonusThrows() + ")");
        return result;
        // return frames.size() >= MAX_FRAMES &&
        //         frames.get(MAX_FRAMES - 1).isCompleted() &&
        //        !needsBonusThrows();
    }

    //In case of a strike or spare in player's last frame 
    public boolean needsBonusThrows() {
        if (frames.size() < MAX_FRAMES) return false;

        Frame lastFrame = frames.get(MAX_FRAMES - 1);
        if (!lastFrame.isCompleted()) return false;
        
        if (lastFrame.isStrike()) {
            return getNextThrows(MAX_FRAMES - 1, 3).size() < 3;
        } else if (lastFrame.isSpare()) {
            return getNextThrows(MAX_FRAMES - 1, 2).size() < 2;
        }
        
        return false;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Frame> getFrames() {
        return new ArrayList<>(frames);
    }
    
    public Frame getCurrentFrame() {
        return currentFrame;
    }
}