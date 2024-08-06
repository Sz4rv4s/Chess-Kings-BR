package game.console;

import model.BoardGameModel;
import model.Position;
import game.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleBoardGameTest {

    @Test
    public void testParseMoveValidInput() {
        Position position = ConsoleBoardGame.parseMove("3 5");
        assertNotNull(position);
        assertEquals("(3,5)", position.toString());
    }

    @Test
    public void testParseMoveValidInputWithSpaces() {
        Position position = ConsoleBoardGame.parseMove("  7  8  ");
        assertNotNull(position);
        assertEquals("(7,8)", position.toString());
    }

    @Test
    public void testParseMoveInvalidInputNonNumeric() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConsoleBoardGame.parseMove("a b");
        });
    }

    @Test
    public void testParseMoveInvalidInputSingleNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConsoleBoardGame.parseMove("10");
        });
    }

    @Test
    public void testParseMoveInvalidInputEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConsoleBoardGame.parseMove("");
        });
    }

    private static class TestBoardGameModel extends BoardGameModel {
        private final State.Player winner;

        TestBoardGameModel(State.Player winner) {
            this.winner = winner;
        }

        @Override
        public State.Player determineWinner() {
            return winner;
        }
    }
}
