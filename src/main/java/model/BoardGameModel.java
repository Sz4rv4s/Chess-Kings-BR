package model;

import game.BasicState;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.tinylog.Logger;
import scoreboard.GameResultSaver;

import java.util.Random;

/**
 * The BoardGameModel class represents the model for the board game.
 * It manages the state of the game, including the board, the current player,
 * and game logic such as moves and game status.
 */
public class BoardGameModel implements BasicState<Position> {

    /** The number of rows on the board. */
    public static final int BOARD_ROWS = 6;

    /** The number of columns on the board. */
    public static final int BOARD_COLUMNS = 8;

    private final ReadOnlyObjectWrapper<Square>[][] board;
    public Random random;
    private final StringProperty currentPlayerProperty = new SimpleStringProperty("Player 1");
    private Player currentPlayer;

    /**
     * Initializes a new instance of the BoardGameModel class.
     * Sets up the board and initializes the game state.
     */
    public BoardGameModel() {
        Logger.debug("Initializing BoardGameModel");
        currentPlayer = Player.PLAYER_1;
        currentPlayerProperty.set(currentPlayer.toString());
        board = new ReadOnlyObjectWrapper[BOARD_COLUMNS][BOARD_ROWS];
        random = new Random();
        for (var i = 0; i < BOARD_COLUMNS; i++) {
            for (var j = 0; j < BOARD_ROWS; j++) {
                board[i][j] = new ReadOnlyObjectWrapper<>(
                        switch (i) {
                            case 0 -> (j == 2 ? Square.WHITE : Square.NONE);
                            case 7 -> (j == 3 ? Square.BLACK : Square.NONE);
                            default -> Square.NONE;
                        }
                );
            }
        }
        Logger.debug("BoardGameModel initialized");
    }

    /**
     * Resets the game to its initial state.
     */
    public void reset() {
        Logger.info("Resetting game state");
        currentPlayer = Player.PLAYER_1;
        currentPlayerProperty.set(currentPlayer.toString());
        for (var i = 0; i < BOARD_COLUMNS; i++) {
            for (var j = 0; j < BOARD_ROWS; j++) {
                board[i][j].set(
                        switch (i) {
                            case 0 -> (j == 2 ? Square.WHITE : Square.NONE);
                            case 7 -> (j == 3 ? Square.BLACK : Square.NONE);
                            default -> Square.NONE;
                        }
                );
            }
        }
        Logger.info("Game state reset");
    }

    /**
     * Gets the property representing the current player.
     *
     * @return the current player property
     */
    public StringProperty currentPlayerProperty() {
        return currentPlayerProperty;
    }

    /**
     * Generates a random position on the board.
     *
     * @return a random position
     */
    public Position generateRandomPosition() {
        int row = random.nextInt(BOARD_ROWS);
        int col = random.nextInt(BOARD_COLUMNS);
        Logger.debug("Generated random position: ({}, {})", row, col);
        return new Position(row, col);
    }

    /**
     * Gets the property of the square at the specified position.
     *
     * @param row the row index
     * @param col the column index
     * @return the read-only property of the square at the specified position
     */
    public ReadOnlyObjectProperty<Square> squareProperty(int row, int col) {
        if (isOnBoard(row, col)) {
            return board[col][row].getReadOnlyProperty();
        } else {
            Logger.error("Attempted to access out of bounds position: ({}, {})", row, col);
            throw new ArrayIndexOutOfBoundsException("Position out of bounds: (" + row + ", " + col + ")");
        }
    }

    /**
     * Gets the square at the specified position.
     *
     * @param p the position
     * @return the square at the specified position
     */
    public Square getSquare(Position p) {
        if (isOnBoard(p)) {
            return board[p.col()][p.row()].get();
        } else {
            Logger.error("Attempted to get square at out of bounds position: {}", p);
            throw new ArrayIndexOutOfBoundsException("Position out of bounds: " + p);
        }
    }

    /**
     * Sets the square at the specified position.
     *
     * @param p the position
     * @param square the square to set
     */
    public void setSquare(Position p, Square square) {
        Logger.debug("Setting square at position {} to {}", p, square);
        board[p.col()][p.row()].set(square);
    }

    /**
     * Checks if the specified position is on the board.
     *
     * @param row the row index
     * @param col the column index
     * @return true if the position is on the board, false otherwise
     */
    public static boolean isOnBoard(int row, int col) {
        return 0 <= row && row < BOARD_ROWS && 0 <= col && col < BOARD_COLUMNS;
    }

    /**
     * Checks if the specified position is on the board.
     *
     * @param position the position
     * @return true if the position is on the board, false otherwise
     */
    public static boolean isOnBoard(Position position) {
        if (position == null) return false;
        return isOnBoard(position.row(), position.col());
    }

    /**
     * Finds the current player's position on the board.
     *
     * @return the current player's position, or null if not found
     */
    public Position findCurrentPlayerPosition() {
        Square playerSquare = (currentPlayer == Player.PLAYER_1) ? Square.WHITE : Square.BLACK;
        for (int col = 0; col < BOARD_COLUMNS; col++) {
            for (int row = 0; row < BOARD_ROWS; row++) {
                if (board[col][row].get() == playerSquare) {
                    Position position = new Position(row, col);
                    Logger.debug("Current player position: {}", position);
                    return position;
                }
            }
        }
        Logger.warn("Current player position not found");
        return null;
    }

    /**
     * Checks if the specified position is empty.
     *
     * @param p the position
     * @return true if the position is empty, false otherwise
     */
    public boolean isEmpty(Position p) {
        boolean empty = isOnBoard(p) && getSquare(p) == Square.NONE;
        Logger.debug("Position {} is empty: {}", p, empty);
        return empty;
    }

    /**
     * Checks if the specified position is clear.
     *
     * @param p the position
     * @return true if the position is clear, false otherwise
     */
    public boolean isClear(Position p) {
        boolean clear = getSquare(p) == Square.CLEAR;
        Logger.debug("Position {} is clear: {}", p, clear);
        return clear;
    }

    /**
     * Checks if the move to the specified position is a "king move".
     *
     * @param to the position to move to
     * @return true if the move is a king move, false otherwise
     */
    public boolean isKingMove(Position to) {
        Position from = findCurrentPlayerPosition();
        if (from == null) {
            Logger.warn("Current player position not found for king move check");
            return false;
        }
        int dx = Math.abs(to.row() - from.row());
        int dy = Math.abs(to.col() - from.col());
        boolean isKingMove = dx + dy == 1 || dx * dy == 1;
        Logger.debug("Move from {} to {} is king move: {}", from, to, isKingMove);
        return isKingMove;
    }

    /**
     * Clears a random square on the board.
     */
    public void clearRandomSquare() {
        Logger.info("Clearing random square");
        Position randomSquare;
        do {
            randomSquare = generateRandomPosition();
        } while (!isEmpty(randomSquare));
        setSquare(randomSquare, Square.CLEAR);
        Logger.info("Cleared square at position {}", randomSquare);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var j = 0; j < BOARD_ROWS; j++) {
            for (var i = 0; i < BOARD_COLUMNS; i++) {
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public Player getNextPlayer() {
        return currentPlayer;
    }

    @Override
    public boolean isGameOver() {
        Logger.debug("Checking if game is over");
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                Position from = new Position(row, col);
                if (getSquare(from) == (currentPlayer == Player.PLAYER_1 ? Square.WHITE : Square.BLACK)) {
                    for (int dRow = -1; dRow <= 1; dRow++) {
                        for (int dCol = -1; dCol <= 1; dCol++) {
                            Position to = new Position(row + dRow, col + dCol);
                            if (isOnBoard(to) && isLegalMove(to)) {
                                Logger.debug("Game is not over, legal move found from {} to {}", from, to);
                                return false;
                            }
                        }
                    }
                }
            }
        }
        Logger.info("Game is over");
        return true;
    }

    @Override
    public Status getStatus() {
        if (!isGameOver()) {
            return Status.IN_PROGRESS;
        }
        return null;
    }

    @Override
    public boolean isWinner(Player player) {
        if (!isGameOver()) {
            return false;
        }
        boolean isWinner = !currentPlayer.equals(player);
        Logger.debug("Player {} is winner: {}", player, isWinner);
        return isWinner;
    }

    /**
     * Checks if it is legal to move from the current player's position.
     *
     * @return true if it is legal to move from the current player's position, false otherwise
     */
    public boolean isLegalToMoveFrom() {
        Position from = findCurrentPlayerPosition();
        boolean legal = isOnBoard(from) && !isEmpty(from) && !isClear(from);
        Logger.debug("It is legal to move from {}: {}", from, legal);
        return legal;
    }

    @Override
    public boolean isLegalMove(Position to) {
        boolean legal = isLegalToMoveFrom() && isOnBoard(to) && isEmpty(to) && isKingMove(to) && !isClear(to);
        Logger.debug("Move to {} is legal: {}", to, legal);
        return legal;
    }

    @Override
    public void makeMove(Position to) {
        Logger.info("Making move to position {}", to);
        Position from = findCurrentPlayerPosition();
        if (isLegalMove(to)) {
            setSquare(to, getSquare(from));
            setSquare(from, Square.NONE);
            clearRandomSquare();
            switchPlayer();
        } else {
            Logger.warn("Illegal move attempted to position {}", to);
        }
    }

    /**
     * Switches the current player to the opponent.
     */
    public void switchPlayer() {
        Logger.info("Switching player");
        currentPlayer = currentPlayer.opponent();
        currentPlayerProperty.set(currentPlayer.toString());
        Logger.info("Current player is now {}", currentPlayer);
    }

    /**
     * Checks for a winner at the end of the game.
     */
    public void checkForWinner() {
        Logger.info("Checking for winner");
        if (isGameOver()) {
            Player winner = determineWinner();
            if (winner != null) {
                game.gui.BoardGameController.displayWinner(winner);
                Logger.info("Winner determined: {}", winner);
            } else {
                Logger.warn("Game over but no winner determined");
            }
        }
    }

    /**
     * Determines the winner of the game.
     *
     * @return the winning player, or null if there is no winner
     */
    public Player determineWinner() {
        if (isWinner(Player.PLAYER_1)) {
            return Player.PLAYER_1;
        } else if (isWinner(Player.PLAYER_2)) {
            return Player.PLAYER_2;
        } else {
            return null;
        }
    }

    /**
     * Saves the game result.
     *
     * @param winner the name of the winning player
     */
    public void saveGameResult(String winner) {
        Logger.info("Saving game result, winner: {}", winner);
        GameResultSaver.saveResult(winner);
        Logger.info("Saved game result: {}", winner);
    }

    /**
     * The main method for testing the BoardGameModel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        var model = new BoardGameModel();
        System.out.println(model);
        Logger.info("BoardGameModel main method executed");
    }
}