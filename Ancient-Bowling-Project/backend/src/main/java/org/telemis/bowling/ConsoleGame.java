package org.telemis.bowling;

import java.util.Scanner;

import org.telemis.bowling.model.Frame;
import org.telemis.bowling.model.Game;
import org.telemis.bowling.model.Player;

public class ConsoleGame {
    private final Game game;
    private final Scanner scanner;

    public ConsoleGame() {
        this.game = new Game();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Ancient African Bowling!");
        setupPlayers();
        game.start();
        playGame();
        showFinalScores();
    }

    private void setupPlayers() {
        System.out.println("Enter the number of players (minimum 2):");
        int numPlayers = getValidIntInput(2, 10);

        for (int i = 1; i <= numPlayers; i++) {
            System.out.printf("Enter name for Player %d: ", i);
            String name = scanner.nextLine();
            game.addPlayer(name);
        }
    }

    private void playGame() {
        while (!game.isGameComplete()) {
            Player currentPlayer = game.getCurrentPlayer();
            // BUG NOTE: If the previous frame is completed, we must ensure a new frame is started before asking for input.
            // Otherwise, the UI will show the remaining pins from the old frame, not a fresh 15.
            currentPlayer.ensureFreshFrame();
            Frame currentFrame = currentPlayer.getCurrentFrame();
            long completedFrames = currentPlayer.getFrames().stream().filter(Frame::isCompleted).count();
            int frameNumber = (int) completedFrames + 1;

            System.out.println("\n" + "=".repeat(40));
            System.out.printf("Current Player: %s (Frame %d)\n", currentPlayer.getName(), frameNumber);
            System.out.printf("Current Score: %d\n", currentPlayer.calculateScore());

            System.out.printf("Enter number of pins knocked down: (0 - %d): ", currentFrame.getRemainingPins());
            int pins = getValidIntInput(0, currentFrame.getRemainingPins());
            
            try {
                game.addThrow(pins);
                displayThrowResult(pins, currentFrame);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid throw: " + e.getMessage());
            }
            System.out.println("DEBUG - Remaining pins:" + currentFrame.getRemainingPins());
        }
    }

    private void displayThrowResult(int pins, Frame currentFrame) {
        if (pins == 15) {
            System.out.println("STRIKE!");
        } else if (currentFrame.isSpare()) {
            System.out.println("SPARE!");
        } else {
            System.out.printf("Knocked down %d pins\n", pins);
        }
    }

    private void showFinalScores() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Game Over! Final Scores:");
        System.out.println("=".repeat(40));

        var scoreboard = game.getScoreboard();
        for (int i = 0; i < scoreboard.size(); i++) {
            var playerScore = scoreboard.get(i);
            System.out.printf("%d. %s: %d points\n", i + 1, playerScore.name(), playerScore.score());
        }
    }

    private int getValidIntInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Please enter a number between %d and %d\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }   
        }
    }

    public static void main(String[] args) {
        new ConsoleGame().start();
    }
} 