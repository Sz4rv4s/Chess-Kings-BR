package model;

import game.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardGameModelTest {

    private BoardGameModel model;

    @BeforeEach
    void setUp() {
        model = new BoardGameModel();
    }

    @Test
    void testGenerateRandomPosition() {
        Position position = model.generateRandomPosition();
        assertTrue(position.row() >= 0 && position.row() < BoardGameModel.BOARD_ROWS);
        assertTrue(position.col() >= 0 && position.col() < BoardGameModel.BOARD_COLUMNS);
    }

    @Test
    void testSquareProperty() {
        Position position = new Position(0, 0);
        assertNotNull(model.squareProperty(position.row(), position.col()));
    }

    @Test
    void testGetSquare() {
        Position position = new Position(0, 0);
        assertEquals(Square.NONE, model.getSquare(position));
    }

    @Test
    void testSetSquare() {
        Position position = new Position(0, 0);
        model.setSquare(position, Square.WHITE);
        assertEquals(Square.WHITE, model.getSquare(position));
    }

    @Test
    void testIsOnBoard() {
        assertTrue(BoardGameModel.isOnBoard(0, 0));
        assertFalse(BoardGameModel.isOnBoard(-1, 0));
        assertFalse(BoardGameModel.isOnBoard(0, -1));
        assertFalse(BoardGameModel.isOnBoard(BoardGameModel.BOARD_ROWS, 0));
        assertFalse(BoardGameModel.isOnBoard(0, BoardGameModel.BOARD_COLUMNS));
    }

    @Test
    void testIsOnBoardPosition() {
        Position position = new Position(0, 0);
        assertTrue(BoardGameModel.isOnBoard(position));
    }

    @Test
    void testFindCurrentPlayerPosition() {
        assertNotNull(model.findCurrentPlayerPosition());
    }

    @Test
    void testIsEmpty() {
        Position position = new Position(0, 0);
        assertTrue(model.isEmpty(position));
    }

    @Test
    void testIsClear() {
        Position position = new Position(0, 0);
        assertFalse(model.isClear(position));
    }

    @Test
    void testIsKingMove() {
        Position to = new Position(1, 1);
        assertTrue(model.isKingMove(to));
    }

    @Test
    void testIsGameOver() {
        assertFalse(model.isGameOver());
    }

    @Test
    void testIsLegalToMoveFrom() {
        assertTrue(model.isLegalToMoveFrom());
    }

    @Test
    void testIsLegalMove() {
        Position to = new Position(1, 1);
        assertTrue(model.isLegalMove(to));
    }

    @Test
    void testMakeMove() {
        Position to = new Position(1, 1);
        Square beforeMove = model.getSquare(to);
        model.makeMove(to);
        assertNotEquals(beforeMove, model.getSquare(to));
    }

    @Test
    void testSwitchPlayer() {
        State.Player currentPlayer = model.getNextPlayer();
        model.switchPlayer();
        assertNotEquals(currentPlayer, model.getNextPlayer());
    }

    @Test
    void testCheckForWinner() {
        model.checkForWinner();
        assertNull(model.determineWinner());
    }

    @Test
    void testDetermineWinner() {
        assertNull(model.determineWinner());
    }

    @Test
    void testReset() {
        model.setSquare(new Position(0, 0), Square.BLACK);
        model.reset();
        assertEquals(Square.NONE, model.getSquare(new Position(0, 0)));
        assertEquals(State.Player.PLAYER_1, model.getNextPlayer());
    }

    @Test
    void testSaveGameResult() {
        assertDoesNotThrow(() -> model.saveGameResult("Player 1"));
    }

    @Test
    void testCurrentPlayerProperty() {
        assertNotNull(model.currentPlayerProperty());
    }

    @Test
    void testToString() {
        String boardString = model.toString();
        assertNotNull(boardString);
        assertEquals(BoardGameModel.BOARD_ROWS, boardString.split("\n").length);
    }
}
