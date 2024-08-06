package login;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import game.gui.BoardGameApplication;
import org.tinylog.Logger;

/**
 * The LoginController class handles the logic for the login screen of the BoardGameApplication.
 * It initializes the login screen, handles button actions, and interacts with the main application.
 */
public class LoginController {

    @FXML
    private TextField player1NameField;

    @FXML
    private TextField player2NameField;

    @FXML
    private Button startButton;

    @FXML
    private Button scoreboardButton;

    private BoardGameApplication mainApp;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        startButton.setOnAction(event -> handleStart());
        scoreboardButton.setOnAction(event -> handleScoreboard());
        Logger.info("LoginController initialized.");
    }

    /**
     * Sets the main application reference to this controller.
     *
     * @param mainApp the main application
     */
    public void setMainApp(BoardGameApplication mainApp) {
        this.mainApp = mainApp;
        Logger.info("Main application set in LoginController.");
    }

    /**
     * Handles the action when the start button is clicked.
     * Retrieves player names from text fields and starts the game screen.
     */
    private void handleStart() {
        String player1Name = player1NameField.getText();
        String player2Name = player2NameField.getText();
        Logger.info("Start button clicked. Player1: {}, Player2: {}", player1Name, player2Name);
        mainApp.showGameScreen(player1Name, player2Name);
    }

    /**
     * Handles the action when the scoreboard button is clicked.
     * Shows the scoreboard screen.
     */
    private void handleScoreboard() {
        Logger.info("Scoreboard button clicked.");
        mainApp.showScoreboardScreen();
    }
}
