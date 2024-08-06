package game.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.BoardGameModel;
import model.Position;
import model.Square;
import game.State.Player;
import org.tinylog.Logger;
import util.BoardGameMoveSelector;
import util.BoardGameMoveSelector.Phase;
import util.EnumImageStorage;
import util.ImageStorage;

/**
 * The {@code BoardGameController} class is responsible for handling the logic and UI interactions
 * for the board game. It manages the game board, updates the UI based on game state, and handles user interactions.
 */
public class BoardGameController {

    private static final int SQUARE_SIZE = 45;

    @FXML
    private GridPane board;

    @FXML
    private Label currentPlayerLabel;

    private static final BoardGameModel model = new BoardGameModel();
    private final BoardGameMoveSelector selector = new BoardGameMoveSelector(model);

    private static String player1Name;
    private static String player2Name;
    private static BoardGameApplication mainApp;

    private final ImageStorage<Square> imageStorage = new EnumImageStorage<>(Square.class);

    /**
     * Sets the main application instance.
     *
     * @param mainApp the main application
     */
    public void setMainApp(BoardGameApplication mainApp) {
        this.mainApp = mainApp;
        Logger.info("Main application set.");
    }

    /**
     * Sets the names of the players.
     *
     * @param player1Name the name of the first player
     * @param player2Name the name of the second player
     */
    public void setPlayerNames(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        currentPlayerLabel.setText("Current player: " + player1Name);
        bindCurrentPlayerLabel();
        Logger.info("Player names set: player1Name = {}, player2Name = {}", player1Name, player2Name);
    }

    /**
     * Binds the current player label to the current player property of the model.
     * Updates the label with the name of the current player.
     */
    private void bindCurrentPlayerLabel() {
        StringBinding currentPlayerText = Bindings.createStringBinding(() -> {
            String currentPlayer = model.currentPlayerProperty().get();
            return "Current player: " + (currentPlayer.equals(Player.PLAYER_1.toString()) ? player1Name : player2Name);
        }, model.currentPlayerProperty());

        currentPlayerLabel.textProperty().bind(currentPlayerText);
        Logger.info("Current player label bound.");
    }

    /**
     * Initializes the controller and sets up the board and game state listeners.
     */
    @FXML
    private void initialize() {
        initializeBoard();
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);
        Logger.info("BoardGameController initialized.");
    }

    /**
     * Initializes the game board with squares and pieces.
     * Sets up the board grid and event listeners for user interactions.
     */
    private void initializeBoard() {
        board.getChildren().clear();
        for (var row = 0; row < BoardGameModel.BOARD_ROWS; row++) {
            for (var col = 0; col < BoardGameModel.BOARD_COLUMNS; col++) {
                var square = createSquare(row, col);
                GridPane.setColumnIndex(square, col);
                GridPane.setRowIndex(square, row);
                board.add(square, col, row);
                Logger.info("Added square at row {}, column {}", row, col);
            }
        }
    }

    /**
     * Creates a square on the board at the specified row and column.
     *
     * @param row the row index
     * @param col the column index
     * @return the created {@code StackPane} representing the square
     */
    private StackPane createSquare(int row, int col) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        square.backgroundProperty().bind(createSquareBackgroundBinding(model.squareProperty(row, col)));

        var piece = new ImageView();
        piece.setFitWidth(SQUARE_SIZE);
        piece.setFitHeight(SQUARE_SIZE);
        piece.setPreserveRatio(true);
        piece.imageProperty().bind(createImageBinding(model.squareProperty(row, col)));

        piece.setSmooth(true);
        piece.setCache(true);

        square.getChildren().add(piece);
        GridPane.setRowIndex(square, row);
        GridPane.setColumnIndex(square, col);
        square.setOnMouseClicked(this::handleMouseClick);
        Logger.info("Created square at row {}, column {}", row, col);
        return square;
    }

    /**
     * Handles mouse click events on the squares of the board.
     * Processes the click to select a position for moving a piece.
     *
     * @param event the mouse click event
     */
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);

        if (row == null || col == null) {
            Logger.error("Child does not have row or column index set: {}", square);
        } else {
            Logger.info("Click on square {}, {}", row, col);
            Position clickedPosition = new Position(row, col);
            selector.select(clickedPosition);

            if (selector.isReadyToMove()) {
                Logger.info("Making move from {} to {}", selector.getFrom(), selector.getTo());
                selector.makeMove();
                model.checkForWinner();
            }
        }
    }

    /**
     * Creates a binding for the image to be displayed on a square.
     * The image corresponds to the piece on that square.
     *
     * @param squareProperty the property representing the square
     * @return the {@code ObjectBinding<Image>} for the square's image
     */
    private ObjectBinding<Image> createImageBinding(ReadOnlyObjectProperty<Square> squareProperty) {
        return new ObjectBinding<>() {
            {
                super.bind(squareProperty);
            }

            @Override
            protected Image computeValue() {
                return imageStorage.get(squareProperty.get());
            }
        };
    }

    /**
     * Creates a binding for the background color of a square.
     * The background color indicates whether the square is empty or not.
     *
     * @param squareProperty the property representing the square
     * @return the {@code ObjectBinding<Background>} for the square's background
     */
    private ObjectBinding<Background> createSquareBackgroundBinding(ReadOnlyObjectProperty<Square> squareProperty) {
        return new ObjectBinding<>() {
            {
                super.bind(squareProperty);
            }

            @Override
            protected Background computeValue() {
                return squareProperty.get() == Square.CLEAR
                        ? new Background(new BackgroundFill(Color.BLACK, null, null))
                        : new Background(new BackgroundFill(Color.TRANSPARENT, null, null));
            }
        };
    }

    /**
     * Shows changes in the selection phase on the board.
     *
     * @param value    the observable value representing the current phase
     * @param oldPhase the previous phase
     * @param newPhase the new phase
     */
    private void showSelectionPhaseChange(ObservableValue<? extends Phase> value, Phase oldPhase, Phase newPhase) {
        Logger.info("Selection phase changed from {} to {}", oldPhase, newPhase);
        updateSelectionVisuals();
    }

    /**
     * Updates the visual representation of the selection on the board.
     * Highlights the selected square.
     */
    private void updateSelectionVisuals() {
        for (var child : board.getChildren()) {
            child.getStyleClass().remove("selected");
        }

        if (selector.getPhase() == Phase.SELECT_TO) {
            showSelection(selector.getFrom());
        }
        Logger.info("Selection visuals updated.");
    }

    /**
     * Highlights the square at the specified position to show it as selected.
     *
     * @param position the position of the square to highlight
     */
    private void showSelection(Position position) {
        var square = getSquare(position);
        if (square != null) {
            square.getStyleClass().add("selected");
            Logger.info("Showing selection at position {}", position);
        } else {
            Logger.error("No square found at position: {}", position);
        }
    }

    /**
     * Retrieves the {@code StackPane} representing the square at the given position.
     *
     * @param position the position of the square
     * @return the {@code StackPane} representing the square, or {@code null} if not found
     */
    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            Integer colIndex = GridPane.getColumnIndex(child);

            if (rowIndex != null && colIndex != null && rowIndex == position.row() && colIndex == position.col()) {
                return (StackPane) child;
            }
        }
        Logger.error("No square found at position: {}", position);
        return null;
    }

    /**
     * Displays the winner of the game in a dialog.
     * Provides options to exit the game or view the scoreboard.
     *
     * @param winner the player who won the game
     */
    public static void displayWinner(Player winner) {
        String winnerName = (winner == Player.PLAYER_1) ? player1Name : player2Name;
        Logger.info("Displaying winner: {}", winnerName);
        model.saveGameResult(winnerName);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Congratulations!");
        alert.setContentText("Winner: " + winnerName);

        ButtonType exitButton = new ButtonType("Exit");
        ButtonType scoreboardButton = new ButtonType("Scoreboard");
        alert.getButtonTypes().setAll(exitButton, scoreboardButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == exitButton) {
                Logger.info("Exiting the game.");
                Platform.exit();
            } else if (response == scoreboardButton) {
                Logger.info("Showing scoreboard screen.");
                mainApp.showScoreboardScreen();
                model.reset();
            }
        });
    }
}
