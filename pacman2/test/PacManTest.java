import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PacManTest {
    private MemoryHighScoreStore highScoreStore;
    private PacMan game;
    private KeyListener input;

    @BeforeEach
    void setUp() {
        highScoreStore = new MemoryHighScoreStore(300);
        game = new PacMan(new SoundManager(false), highScoreStore);
        game.gameLoop.stop();
        input = game.getKeyListeners()[0];
    }

    @AfterEach
    void tearDown() {
        game.gameLoop.stop();
    }

    @Test
    void loadsMapEntitiesFromMazeData() {
        int expectedFoods = 0;
        MazeMap map = new MazeMap();
        for (int row = 0; row < MazeMap.ROW_COUNT; row += 1) {
            for (int column = 0; column < MazeMap.COLUMN_COUNT; column += 1) {
                if (map.tileAt(row, column) == ' ') {
                    expectedFoods += 1;
                }
            }
        }

        assertNotNull(game.pacman);
        assertEquals(9*MazeMap.TILE_SIZE, game.pacman.x);
        assertEquals(15*MazeMap.TILE_SIZE, game.pacman.y);
        assertEquals(4, game.ghosts.size());
        assertEquals(4, game.powerPellets.size());
        assertEquals(expectedFoods, game.foods.size());
    }

    @Test
    void detectsOverlapAndSeparation() {
        GameEntity first = new GameEntity(null, 0, 0, 32, 32);
        GameEntity overlapping = new GameEntity(null, 31, 0, 32, 32);
        GameEntity separate = new GameEntity(null, 32, 0, 32, 32);

        assertTrue(game.collision(first, overlapping));
        assertFalse(game.collision(first, separate));
    }

    @Test
    void updatesAndPersistsOnlyNewHighScores() throws Exception {
        invoke("addScore", new Class<?>[] {int.class}, 200);
        assertEquals(200, game.session.score);
        assertEquals(300, game.session.highScore);
        assertEquals(0, highScoreStore.saveCount);

        invoke("addScore", new Class<?>[] {int.class}, 150);
        assertEquals(350, game.session.score);
        assertEquals(350, game.session.highScore);
        assertEquals(350, highScoreStore.highScore);
        assertEquals(1, highScoreStore.saveCount);
    }

    @Test
    void handlesStartPauseResumeMuteAndRestartCommands() {
        press(KeyEvent.VK_SPACE);
        assertEquals(GameState.PLAYING, game.session.state);

        press(KeyEvent.VK_P);
        assertEquals(GameState.PAUSED, game.session.state);
        press(KeyEvent.VK_P);
        assertEquals(GameState.PLAYING, game.session.state);

        press(KeyEvent.VK_M);
        assertTrue(game.soundManager.isMuted());

        game.session.score = 500;
        game.session.highScore = 500;
        game.session.level = 4;
        game.session.lives = 1;
        press(KeyEvent.VK_R);
        assertEquals(GameState.READY, game.session.state);
        assertEquals(0, game.session.score);
        assertEquals(500, game.session.highScore);
        assertEquals(1, game.session.level);
        assertEquals(3, game.session.lives);
    }

    @Test
    void buffersBlockedTurnAndAcceptsValidTurn() throws Exception {
        press(KeyEvent.VK_SPACE);
        press(KeyEvent.VK_DOWN);
        invoke("applyRequestedDirection", new Class<?>[0]);
        assertEquals('R', game.pacman.direction);
        assertEquals(8, game.pacman.velocityX);

        press(KeyEvent.VK_LEFT);
        invoke("applyRequestedDirection", new Class<?>[0]);
        assertEquals('L', game.pacman.direction);
        assertEquals(-8, game.pacman.velocityX);
    }

    @Test
    void appliesDocumentedLevelDifficultyLimits() throws Exception {
        game.session.level = 2;
        invoke("applyLevelDifficulty", new Class<?>[0]);
        assertEquals(6500, readInt("powerModeDuration"));
        assertEquals(6500, readInt("scatterModeDuration"));
        assertEquals(21000, readInt("chaseModeDuration"));

        game.session.level = 20;
        invoke("applyLevelDifficulty", new Class<?>[0]);
        assertEquals(3000, readInt("powerModeDuration"));
        assertEquals(3000, readInt("scatterModeDuration"));
        assertEquals(30000, readInt("chaseModeDuration"));
    }

    @Test
    void movesGhostsOnFourOfEveryFiveFrames() throws Exception {
        int movementFrames = 0;
        for (int frame = 0; frame < 10; frame += 1) {
            if ((boolean) invoke("shouldMoveGhosts", new Class<?>[0])) {
                movementFrames += 1;
            }
        }

        assertEquals(8, movementFrames);
    }

    private void press(int keyCode) {
        input.keyPressed(new KeyEvent(game, KeyEvent.KEY_PRESSED,
                1L, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
    }

    private Object invoke(String methodName, Class<?>[] parameterTypes, Object... arguments)
            throws Exception {
        Method method = PacMan.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(game, arguments);
    }

    private int readInt(String fieldName) throws Exception {
        Field field = PacMan.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getInt(game);
    }

    private static final class MemoryHighScoreStore implements HighScoreStore {
        int highScore;
        int saveCount;

        MemoryHighScoreStore(int highScore) {
            this.highScore = highScore;
        }

        @Override
        public int load() {
            return highScore;
        }

        @Override
        public void save(int highScore) {
            this.highScore = highScore;
            saveCount += 1;
        }
    }
}
