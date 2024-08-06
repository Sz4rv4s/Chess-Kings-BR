package model;

/**
 * The Position class represents a position on the game board.
 * It encapsulates the row and column indices of the position.
 *
 * @param row the row index of the position
 * @param col the column index of the position
 */
public record Position(int row, int col) {

    /**
     * Returns a string representation of the position in the format "(row,col)".
     *
     * @return a string representing the position
     */
    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

}
