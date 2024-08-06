package game.console;

import model.BoardGameModel;
import model.Position;

import static game.console.ConsoleBoardGame.printWinner;
/**
 * The {@code Main} class serves as the entry point for the console-based board game application.
 * It initializes the game model and starts the game loop.
 */
public class Main {
    /**
     * The main method initializes the board game model, sets up the game loop, and starts the game.
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        var model = new BoardGameModel();
        var game = new BasicGame<Position>(model, ConsoleBoardGame::parseMove) {
            @Override
            protected void makeMoveIfPossible(Position position) {
                if (model.isLegalMove(position)) {
                    model.makeMove(position);
                    System.out.println(model);
                    if (model.isGameOver()) {
                        printWinner(model);
                    }
                }
            }
        };
        game.start();
    }
}
