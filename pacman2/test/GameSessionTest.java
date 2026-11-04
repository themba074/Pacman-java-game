import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GameSessionTest {
    @Test
    void clampsNegativeLoadedHighScore() {
        GameSession session = new GameSession(-100);

        assertEquals(0, session.highScore);
    }

    @Test
    void resetRunPreservesHighScoreAndRestoresDefaults() {
        GameSession session = new GameSession(900);
        session.score = 500;
        session.lives = 1;
        session.level = 4;
        session.state = GameState.GAME_OVER;

        session.resetRun();

        assertEquals(0, session.score);
        assertEquals(900, session.highScore);
        assertEquals(3, session.lives);
        assertEquals(1, session.level);
        assertEquals(GameState.READY, session.state);
    }
}
