package scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameResultSaverTest {

    private static final String TEST_FILE_PATH = "test_game_results.json";
    private static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    void setup() {
        GameResultSaver.setFilePath(TEST_FILE_PATH);
    }

    @AfterAll
    void tearDown() throws IOException {
        Files.deleteIfExists(new File(TEST_FILE_PATH).toPath());
    }

    @BeforeEach
    void init() {
        GameResultSaver.clearScores();
    }

    @Test
    void testInitialScores() {
        Map<String, Integer> scores = new GameResultSaver().getScores();
        assertTrue(scores.isEmpty(), "Scores should be empty initially");
    }

    @Test
    void testSaveResult() {
        GameResultSaver.saveResult("Player1");
        Map<String, Integer> scores = new GameResultSaver().getScores();
        assertEquals(1, scores.get("Player1"), "Player1 should have 1 win");

        GameResultSaver.saveResult("Player1");
        scores = new GameResultSaver().getScores();
        assertEquals(2, scores.get("Player1"), "Player1 should have 2 wins");
    }

    @Test
    void testSaveAndLoadScores() throws IOException {
        GameResultSaver.saveResult("Player2");
        GameResultSaver.saveResult("Player2");

        GameResultSaver.loadScores();

        Map<String, Integer> scores = new GameResultSaver().getScores();
        assertEquals(2, scores.get("Player2"), "Player2 should have 2 wins");
    }
}
