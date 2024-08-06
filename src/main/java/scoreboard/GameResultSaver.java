package scoreboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The GameResultSaver class manages the saving and loading of game scores to and from a JSON file.
 */
public class GameResultSaver {

    private static String filePath = "game_results.json";
    private static Map<String, Integer> scores = new HashMap<>();

    static {
        Logger.debug("Initializing GameResultSaver...");
        loadScores();
    }

    /**
     * Sets the file path for the scores file and loads scores from it.
     *
     * @param path the path to the scores file
     */
    public static void setFilePath(String path) {
        Logger.info("Setting file path to {}", path);
        filePath = path;
        loadScores();
    }

    /**
     * Clears all saved scores and saves the empty scores to the file.
     */
    public static void clearScores() {
        Logger.info("Clearing all scores");
        scores = new HashMap<>();
        saveScores();
    }

    /**
     * Loads scores from the specified file. If the file does not exist, initializes an empty scores map.
     */
    public static void loadScores() {
        Logger.info("Loading scores from file: {}", filePath);
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                scores = mapper.readValue(file, new TypeReference<Map<String, Integer>>() {});
                Logger.info("Scores loaded successfully from {}", filePath);
            } else {
                Logger.warn("Scores file not found. Initializing new scores.");
                scores = new HashMap<>();
            }
        } catch (IOException e) {
            Logger.error("Failed to load scores from file: {}", e.getMessage(), e);
            scores = new HashMap<>();
        }
    }

    /**
     * Saves the current scores to the specified file.
     */
    private static void saveScores() {
        Logger.info("Saving scores to file: {}", filePath);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(filePath), scores);
            Logger.info("Scores saved successfully to {}", filePath);
        } catch (IOException e) {
            Logger.error("Failed to save scores to file: {}", e.getMessage(), e);
        }
    }

    /**
     * Saves the result of a game by incrementing the score for the winning player.
     *
     * @param winnerName the name of the winning player
     */
    public static void saveResult(String winnerName) {
        Logger.info("Saving result for winner: {}", winnerName);
        scores.put(winnerName, scores.getOrDefault(winnerName, 0) + 1);
        saveScores();
        Logger.debug("Score updated for {}: {}", winnerName, scores.get(winnerName));
    }

    /**
     * Fetches the current scores.
     *
     * @return a map containing the player names and their corresponding scores
     */
    public Map<String, Integer> getScores() {
        Logger.debug("Fetching current scores");
        return scores;
    }
}