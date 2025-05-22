package org.telemis.bowling.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single player in the ancient bowling game.
 * <p>
 * Manages the player's name, frames, throw progression, and score calculation
 * including special rules for strikes and spares in the last frame.
 * </p>
 */
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
     * Ensures a new frame is created if the current one is completed,
     * except in the final frame where bonus throws are handled within the same frame.
     * <p>
     * Should be called before any new throw is added.
     * </p>
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
     * Adds a throw (number of pins knocked down) to the current frame.
     * <p>
     * Handles logic for last-frame bonus throws automatically.
     * Must call {@link #ensureFreshFrame()} before invoking this.
     * </p>
     *
     * @param pins the number of pins knocked down
     * @throws IllegalStateException if the game is complete and no bonus throws are allowed
     */
    public void addThrow(int pins) {
        ensureFreshFrame(); // Create a new frame if the previous one is completed
        if (isGameComplete()) {
            if (!needsBonusThrows()) {
                throw new IllegalStateException("Game is complete");
            }
        }
        // If on the last frame, always add throws to the last frame (including bonus throws)
        if (currentFrame.isLastFrame()) {
            frames.get(MAX_FRAMES - 1).addThrow(pins);
        } else {
            currentFrame.addThrow(pins);
        }
    }

    /**
     * Calculates the total score up to the current frame,
     * including bonuses for strikes and spares.
     *
     * @return the total score
     */
    public int calculateScore() {
        return calculateScore(frames.size());
    }

    /**
     * Calculates the cumulative score up to a specific frame (1-based index).
     * <p>
     * Strike and spare bonuses are included when possible.
     * </p>
     *
     * @param frameNumber the frame number to calculate the score through
     * @return the cumulative score up to that frame
     */
    public int calculateScore(int frameNumber) {
        int totalScore = 0;
        for (int i = 0; i < frameNumber && i < frames.size(); i++) {
            Frame frame = frames.get(i);
            totalScore += frame.getPinsKnockedDown();

            // For the last frame, bonus throws are already included in getPinsKnockedDown
            if (i < MAX_FRAMES - 1) {
                if (frame.isStrike()) {
                    totalScore += calculateStrikeBonus(i);
                } else if (frame.isSpare()) {
                    totalScore += calculateSpareBonus(i);
                }
            }
        }
        return totalScore;
    }

    /**
     * calculates bonus for a strike at the given frame index.
     */
    private int calculateStrikeBonus(int frameIndex) {
        List<Integer> nextThrows = getNextThrows(frameIndex, 3);
        System.out.println("Debug: StrikeBonus calculated: " + nextThrows);
        return nextThrows.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * calculates bonus for a spare at the given frame index.
     */
    private int calculateSpareBonus(int frameIndex) {
        List<Integer> nextThrows = getNextThrows(frameIndex, 2);
        System.out.println("Debug: SpareBonus calculated: " + nextThrows);
        return nextThrows.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Collects the next N throws starting after a given frame index.
     *
     * @param frameIndex the frame index to start after
     * @param count      number of throws to collect
     * @return a list of upcoming throws
     */
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

    /**
     * Determines if the player has completed all 5 frames,
     * including any necessary bonus throws.
     *
     * @return {@code true} if the game is fully complete for the player
     */
    public boolean isGameComplete() {
        if (frames.size() < MAX_FRAMES) {
            return false;
        }

        Frame lastFrame = frames.get(MAX_FRAMES - 1);
        boolean result = lastFrame.isCompleted();

        if (!result) {
            System.out.println("DEBUG: Player " + name + " isGameComplete: " + result +
                    " (frames: " + frames.size() +
                    ", last frame throws: " + lastFrame.getThrows() +
                    ", last frame completed: " + lastFrame.isCompleted() +
                    ", needsBonus: " + needsBonusThrows() + ")");
        }

        return result;
    }

    /**
     * Checks whether the player still needs to perform bonus throws
     * due to a strike or spare in the last frame.
     *
     * @return {@code true} if additional bonus throws are needed
     */
    public boolean needsBonusThrows() {
        if (frames.size() < MAX_FRAMES) return false;

        Frame lastFrame = frames.get(MAX_FRAMES - 1);

        // For last frame, we need to check if we need bonus throws before checking completion
        if (lastFrame.isStrike()) {
            // For strike, we need 3 bonus throws
            return getNextThrows(MAX_FRAMES - 1, 3).size() < 3;
        } else if (lastFrame.isSpare()) {
            // For spare, we need 2 bonus throws
            return getNextThrows(MAX_FRAMES - 1, 2).size() < 2;
        }

        return false;
    }

    /**
     * Returns the player's name.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of all frames played by the player.
     * The list is a copy to preserve immutability.
     *
     * @return list of frames
     */
    public List<Frame> getFrames() {
        return new ArrayList<>(frames);
    }

    /**
     * Returns the current active frame.
     *
     * @return the current frame
     */
    public Frame getCurrentFrame() {
        return currentFrame;
    }
}