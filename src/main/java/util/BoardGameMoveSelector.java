package util;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import model.BoardGameModel;
import model.Position;
import org.tinylog.Logger;

/**
 * The {@code BoardGameMoveSelector} class manages the selection of moves in a board game.
 * It keeps track of the phases of move selection and ensures that only legal moves are selected.
 */
public class BoardGameMoveSelector {

    /**
     * Enum representing the phases of move selection.
     */
    public enum Phase {
        SELECT_FROM,
        SELECT_TO,
        READY_TO_MOVE
    }

    private final BoardGameModel model;
    private final ReadOnlyObjectWrapper<Phase> phase;
    private boolean invalidSelection;
    private Position from;
    private Position to;

    /**
     * Constructs a {@code BoardGameMoveSelector} with the specified model.
     *
     * @param model the {@code BoardGameModel} to use for move selection
     */
    public BoardGameMoveSelector(BoardGameModel model) {
        this.model = model;
        phase = new ReadOnlyObjectWrapper<>(Phase.SELECT_FROM);
        Logger.info("Initializing BoardGameMoveSelector. Starting phase: {}", phase.get());
        selectFrom();
        invalidSelection = false;
        from = model.findCurrentPlayerPosition();
        Logger.debug("Initial position for current player: {}", from);
    }

    /**
     * Gets the current phase of move selection.
     *
     * @return the current phase
     */
    public Phase getPhase() {
        Logger.debug("Getting current phase: {}", phase.get());
        return phase.get();
    }

    /**
     * Gets the phase property as a read-only object property.
     *
     * @return the read-only phase property
     */
    public ReadOnlyObjectProperty<Phase> phaseProperty() {
        return phase.getReadOnlyProperty();
    }

    /**
     * Checks if the move selector is ready to make a move.
     *
     * @return {@code true} if ready to move, otherwise {@code false}
     */
    public boolean isReadyToMove() {
        boolean ready = phase.get() == Phase.READY_TO_MOVE;
        Logger.debug("Checking if ready to move: {}", ready);
        return ready;
    }

    /**
     * Selects a position based on the current phase.
     *
     * @param position the position to select
     */
    public void select(Position position) {
        Logger.info("Selecting position: {}", position);
        switch (phase.get()) {
            case SELECT_FROM -> {
                Logger.debug("Phase is SELECT_FROM. Calling selectFrom()");
                selectFrom();
            }
            case SELECT_TO -> {
                Logger.debug("Phase is SELECT_TO. Calling selectTo({})", position);
                selectTo(position);
            }
            case READY_TO_MOVE -> {
                Logger.error("Illegal state: already in READY_TO_MOVE phase.");
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Selects the 'from' position for the move.
     */
    private void selectFrom() {
        Logger.info("Selecting 'from' position");
        Position position = model.findCurrentPlayerPosition();
        if (model.isLegalToMoveFrom()) {
            from = position;
            phase.set(Phase.SELECT_TO);
            invalidSelection = false;
            Logger.debug("'From' position set to {}. Phase updated to SELECT_TO", from);
        } else {
            invalidSelection = true;
            Logger.warn("Invalid 'from' position selection: {}", position);
        }
    }

    /**
     * Selects the 'to' position for the move.
     *
     * @param position the 'to' position to select
     */
    public void selectTo(Position position) {
        Logger.info("Selecting 'to' position: {}", position);
        if (model.isLegalMove(position)) {
            to = position;
            phase.set(Phase.READY_TO_MOVE);
            invalidSelection = false;
            Logger.debug("'To' position set to {}. Phase updated to READY_TO_MOVE", to);
        } else {
            invalidSelection = true;
            Logger.warn("Invalid 'to' position selection: {}", position);
        }
    }

    /**
     * Gets the 'from' position of the move.
     *
     * @return the 'from' position
     * @throws IllegalStateException if not in a phase where 'from' position is available
     */
    public Position getFrom() {
        if (phase.get() == Phase.SELECT_FROM) {
            Logger.error("Attempt to get 'from' position in SELECT_FROM phase");
            throw new IllegalStateException();
        }
        Logger.debug("Returning 'from' position: {}", from);
        return from;
    }

    /**
     * Gets the 'to' position of the move.
     *
     * @return the 'to' position
     * @throws IllegalStateException if not in READY_TO_MOVE phase
     */
    public Position getTo() {
        if (phase.get() != Phase.READY_TO_MOVE) {
            Logger.error("Attempt to get 'to' position not in READY_TO_MOVE phase");
            throw new IllegalStateException();
        }
        Logger.debug("Returning 'to' position: {}", to);
        return to;
    }

    /**
     * Makes the move from the 'from' position to the 'to' position.
     *
     * @throws IllegalStateException if not in READY_TO_MOVE phase
     */
    public void makeMove() {
        if (phase.get() != Phase.READY_TO_MOVE) {
            Logger.error("Attempt to make a move not in READY_TO_MOVE phase");
            throw new IllegalStateException();
        }
        Logger.info("Making move from {} to {}", from, to);
        model.makeMove(to);
        reset();
    }

    /**
     * Resets the move selector to the initial state.
     */
    public void reset() {
        Logger.info("Resetting BoardGameMoveSelector");
        from = model.findCurrentPlayerPosition();
        to = null;
        phase.set(Phase.SELECT_TO);
        invalidSelection = false;
        Logger.debug("Reset complete. New 'from' position: {}. Phase set to SELECT_TO", from);
    }
}
