package util;

import model.BoardGameModel;
import model.Position;
import model.Square;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardGameMoveSelectorTest {

    private BoardGameModel model;
    private BoardGameMoveSelector selector;

    @BeforeEach
    void setUp() {
        model = new BoardGameModel();
        selector = new BoardGameMoveSelector(model);
    }

    @Test
    void testSelectFrom() {
        Position position = model.findCurrentPlayerPosition();
        selector.select(position);
        assertEquals(BoardGameMoveSelector.Phase.SELECT_TO, selector.getPhase());
    }

    @Test
    void testSelectToValidMove() {
        Position from = model.findCurrentPlayerPosition();
        Position to = new Position(from.row() + 1, from.col() + 1);
        selector.select(from);
        selector.select(to);
        assertEquals(BoardGameMoveSelector.Phase.READY_TO_MOVE, selector.getPhase());
    }

    @Test
    void testSelectToInvalidMove() {
        Position from = model.findCurrentPlayerPosition();
        Position to = new Position(from.row() - 1, from.col() - 1);
        selector.select(from);
        selector.select(to);
        assertEquals(BoardGameMoveSelector.Phase.SELECT_TO, selector.getPhase());
    }

    @Test
    void testMakeMove() {
        Position from = model.findCurrentPlayerPosition();
        Position to = new Position(from.row() + 1, from.col() + 1);
        selector.select(from);
        selector.select(to);
        selector.makeMove();
        assertEquals(Square.NONE, model.getSquare(from));
        assertEquals(Square.WHITE, model.getSquare(to));
    }

    @Test
    void testReset() {
        Position from = model.findCurrentPlayerPosition();
        selector.select(from);
        selector.reset();
        assertEquals(BoardGameMoveSelector.Phase.SELECT_TO, selector.getPhase());
    }
}
