import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Set;

final class GameRenderer {
    void draw(Graphics graphics, GameEntity pacman, Set<GameEntity> ghosts,
              Set<GameEntity> walls, Set<GameEntity> foods,
              Set<GameEntity> powerPellets, GameEntity bonusItem,
              GameSession session, int powerModeTimeRemaining, boolean muted) {
        graphics.drawImage(pacman.image, pacman.x, pacman.y,
                pacman.width, pacman.height, null);

        for (GameEntity ghost : ghosts) {
            graphics.drawImage(ghost.image, ghost.x, ghost.y,
                    ghost.width, ghost.height, null);
        }
        for (GameEntity wall : walls) {
            graphics.drawImage(wall.image, wall.x, wall.y,
                    wall.width, wall.height, null);
        }

        graphics.setColor(Color.WHITE);
        for (GameEntity food : foods) {
            graphics.fillRect(food.x, food.y, food.width, food.height);
        }
        for (GameEntity powerPellet : powerPellets) {
            graphics.drawImage(powerPellet.image, powerPellet.x, powerPellet.y,
                    powerPellet.width, powerPellet.height, null);
        }
        if (bonusItem != null) {
            graphics.drawImage(bonusItem.image, bonusItem.x, bonusItem.y,
                    bonusItem.width, bonusItem.height, null);
        }

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.PLAIN, 18));
        graphics.drawString(buildStatus(session, powerModeTimeRemaining, muted),
                MazeMap.TILE_SIZE/2, MazeMap.TILE_SIZE/2);

        if (session.state == GameState.READY) {
            drawCenteredMessage(graphics, "Press SPACE to start");
        }
        else if (session.state == GameState.PAUSED) {
            drawCenteredMessage(graphics, "Paused - Press P to resume");
        }
        else if (session.state == GameState.GAME_OVER) {
            drawCenteredMessage(graphics,
                    "Game Over: " + session.score + " - Press R to restart");
        }
    }

    String buildStatus(GameSession session, int powerModeTimeRemaining, boolean muted) {
        String status = "Level " + session.level + "  x" + session.lives +
                " Score: " + session.score + " High: " + session.highScore;
        if (powerModeTimeRemaining > 0) {
            int secondsRemaining = (powerModeTimeRemaining + 999)/1000;
            status += " Power: " + secondsRemaining + "s";
        }
        if (muted) {
            status += " Muted";
        }
        return status;
    }

    private void drawCenteredMessage(Graphics graphics, String message) {
        graphics.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (MazeMap.BOARD_WIDTH - metrics.stringWidth(message))/2;
        int y = MazeMap.BOARD_HEIGHT/2;
        graphics.setColor(Color.YELLOW);
        graphics.drawString(message, x, y);
    }
}
