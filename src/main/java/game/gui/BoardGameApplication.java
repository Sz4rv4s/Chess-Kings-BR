package game.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.tinylog.Logger;
import scoreboard.ScoreboardController;

/**
 * The {@code BoardGameApplication} class is the entry point for the JavaFX application.
 * It initializes and displays different screens of the board game application.
 */
public class BoardGameApplication extends Application {

    private Stage primaryStage;

    /**
     * The main entry point for JavaFX applications. This method is called after the application
     * has been instantiated and the JavaFX toolkit has been initialized.
     *
     * @param primaryStage the primary stage for this application, onto which the application scene
     *                     can be set. The primary stage will be passed to the start method by the JavaFX runtime.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Logger.info("Application started. Showing login screen.");
        showLoginScreen();
    }

    /**
     * Shows the login screen of the application.
     * The login screen allows users to enter their credentials and start the game.
     */
    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(BoardGameApplication.class.getResource("/login.fxml"));
            AnchorPane loginPane = loader.load();

            login.LoginController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(loginPane);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();
            Logger.info("Login screen shown.");
        } catch (Exception e) {
            Logger.error(e, "Error showing login screen.");
        }
    }

    /**
     * Shows the game screen where the board game is played.
     *
     * @param player1Name the name of the first player
     * @param player2Name the name of the second player
     */
    public void showGameScreen(String player1Name, String player2Name) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(BoardGameApplication.class.getResource("/game.fxml"));
            AnchorPane gamePane = loader.load();

            BoardGameController controller = loader.getController();
            controller.setPlayerNames(player1Name, player2Name);
            controller.setMainApp(this);

            Scene scene = new Scene(gamePane);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Chess Kings BR by Péter Szarvas");
            primaryStage.show();
            Logger.info("Game screen shown with players: {} and {}", player1Name, player2Name);
        } catch (Exception e) {
            Logger.error(e, "Error showing game screen.");
        }
    }

    /**
     * Shows the scoreboard screen which displays the scores of players.
     */
    public void showScoreboardScreen() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(BoardGameApplication.class.getResource("/scoreboard.fxml"));
            StackPane scoreboardPane = loader.load();

            ScoreboardController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(scoreboardPane);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Scoreboard");
            primaryStage.show();
            Logger.info("Scoreboard screen shown.");
        } catch (Exception e) {
            Logger.error(e, "Error showing scoreboard screen.");
        }
    }
}
