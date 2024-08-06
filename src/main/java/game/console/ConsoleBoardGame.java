package game.console;

import game.State;
import model.BoardGameModel;
import model.Position;
import org.tinylog.Logger;

import java.util.Scanner;

/**
 * The {@code ConsoleBoardGame} class provides methods to interact with the board game through a console interface.
 * It includes functionalities to parse moves and print the winner.
 */
public class ConsoleBoardGame {

    /**
     * Parses a move string in the format "row col" and converts it into a {@link Position} object.
     *
     * @param s the move string, expected in the format "row col"
     * @return the parsed {@link Position} object corresponding to the move
     * @throws IllegalArgumentException if the input string is not in the correct format
     */
    public static Position parseMove(String s) {
        Logger.info("Parsing move: {}", s);
        s = s.trim();
        if (!s.matches("\\d+\\s+\\d+")) {
            Logger.error("Invalid move format: {}", s);
            throw new IllegalArgumentException();
        }
        var scanner = new Scanner(s);
        Position position = new Position(scanner.nextInt(), scanner.nextInt());
        Logger.info("Parsed position: {}", position);
        return position;
    }

    /**
     * Prints the winner of the game to the console and exits the application.
     *
     * @param model the {@link BoardGameModel} containing the current game state
     */
    public static void printWinner(BoardGameModel model) {
        Logger.info("Determining the winner.");
        State.Player winner = model.determineWinner();
        if (winner != null) {
            Logger.info("{} won the game!", winner);
            System.out.println(winner + " won the game!");
        } else {
            Logger.info("The game is a draw.");
            System.out.println("The game is a draw.");
        }
        Logger.info("Exiting the game.");
        System.exit(0);
    }
}
