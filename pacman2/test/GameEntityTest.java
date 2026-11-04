import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GameEntityTest {
    @Test
    void updatesVelocityForEveryDirection() {
        GameEntity entity = new GameEntity(null, 32, 64, 32, 32);

        entity.updateDirection('U');
        assertVelocity(entity, 0, -8);
        entity.updateDirection('D');
        assertVelocity(entity, 0, 8);
        entity.updateDirection('L');
        assertVelocity(entity, -8, 0);
        entity.updateDirection('R');
        assertVelocity(entity, 8, 0);
    }

    @Test
    void resetsToStartingPosition() {
        GameEntity entity = new GameEntity(null, 32, 64, 32, 32);
        entity.x = 160;
        entity.y = 224;

        entity.reset();

        assertEquals(32, entity.x);
        assertEquals(64, entity.y);
    }

    private void assertVelocity(GameEntity entity, int expectedX, int expectedY) {
        assertEquals(expectedX, entity.velocityX);
        assertEquals(expectedY, entity.velocityY);
    }
}
