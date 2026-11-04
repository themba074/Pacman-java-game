import java.awt.Image;

final class GameEntity {
    int x;
    int y;
    int width;
    int height;
    Image image;
    Image normalImage;

    final int startX;
    final int startY;
    char direction = 'U';
    int velocityX = 0;
    int velocityY = 0;
    boolean frightened = false;
    boolean eatenDuringPowerMode = false;
    GhostType ghostType;

    GameEntity(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.normalImage = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
    }

    void updateDirection(char direction) {
        this.direction = direction;
        int movementStep = MazeMap.TILE_SIZE/4;
        if (direction == 'U') {
            velocityX = 0;
            velocityY = -movementStep;
        }
        else if (direction == 'D') {
            velocityX = 0;
            velocityY = movementStep;
        }
        else if (direction == 'L') {
            velocityX = -movementStep;
            velocityY = 0;
        }
        else if (direction == 'R') {
            velocityX = movementStep;
            velocityY = 0;
        }
    }

    void reset() {
        x = startX;
        y = startY;
    }
}

enum GhostType {
    BLUE,
    ORANGE,
    PINK,
    RED
}

enum GhostMode {
    SCATTER,
    CHASE
}
