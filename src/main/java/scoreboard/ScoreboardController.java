package scoreboard;

import game.gui.BoardGameApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.tinylog.Logger;

import java.util.Map;

/**
 * The ScoreboardController class is responsible for managing the scoreboard view.
 * It displays the scores of the players and allows navigation back to the login screen.
 */
public class ScoreboardController {

    @FXML
    private TableView<PlayerScore> scoreboard;

    @FXML
    private TableColumn<?, ?> playerName;

    @FXML
    private TableColumn<?, ?> numberOfWins;

    @FXML
    private Button backToLoginButton;

    private BoardGameApplication mainApp;

    private final GameResultSaver resultSaver = new GameResultSaver();

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        Logger.info("Initializing ScoreboardController");

        playerName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        numberOfWins.setCellValueFactory(new PropertyValueFactory<>("numberOfWins"));

        Logger.debug("Setting up scoreboard table columns");

        ObservableList<PlayerScore> data = FXCollections.observableArrayList();
        Map<String, Integer> results = resultSaver.getScores();
        Logger.info("Loading scores from GameResultSaver");

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            data.add(new PlayerScore(entry.getKey(), entry.getValue()));
            Logger.debug("Added score for player {}: {}", entry.getKey(), entry.getValue());
        }

        scoreboard.setItems(data);
        Logger.info("Scoreboard data populated with {} entries", data.size());

        backToLoginButton.setOnAction(event -> {
            Logger.info("Back to login button clicked");
            mainApp.showLoginScreen();
        });

        Logger.info("ScoreboardController initialized successfully");
    }

    /**
     * Represents a player's score entry in the scoreboard.
     */
    public static class PlayerScore {
        private final String playerName;
        private final Integer numberOfWins;

        /**
         * Constructs a PlayerScore with the specified player name and number of wins.
         *
         * @param playerName  the name of the player
         * @param numberOfWins the number of wins
         */
        public PlayerScore(String playerName, Integer numberOfWins) {
            this.playerName = playerName;
            this.numberOfWins = numberOfWins;
        }

        /**
         * Gets the name of the player.
         *
         * @return the player name
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * Gets the number of wins.
         *
         * @return the number of wins
         */
        public Integer getNumberOfWins() {
            return numberOfWins;
        }
    }

    /**
     * Sets the reference to the main application.
     *
     * @param mainApp the main application instance
     */
    public void setMainApp(BoardGameApplication mainApp) {
        this.mainApp = mainApp;
        Logger.info("Main application reference set in ScoreboardController");
    }
}
