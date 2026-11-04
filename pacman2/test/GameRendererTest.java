import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GameRendererTest {
    @Test
    void buildsNormalStatusText() {
        GameSession session = new GameSession(1200);
        session.score = 450;
        session.level = 3;
        session.lives = 2;

        String status = new GameRenderer().buildStatus(session, 0, false);

        assertEquals("Level 3  x2 Score: 450 High: 1200", status);
    }

    @Test
    void includesRoundedPowerTimeAndMuteState() {
        GameSession session = new GameSession(0);

        String status = new GameRenderer().buildStatus(session, 6001, true);

        assertEquals("Level 1  x3 Score: 0 High: 0 Power: 7s Muted", status);
    }
}
