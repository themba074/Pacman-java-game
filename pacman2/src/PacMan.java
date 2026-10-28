import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    enum GameState {
        READY,
        PLAYING,
        PAUSED,
        GAME_OVER
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;
    private int tunnelRow = 9;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image powerFoodImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, Q = power pellet, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "XQ               QX",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O      XbpoX      O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "XQ               QX",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> powerPellets;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    char requestedDirection = '\0';
    GameState gameState = GameState.READY;
    int powerModeTimeRemaining = 0;
    private int powerModeDuration = 7000;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("./powerFood.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        //how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); //20fps (1000/50)
        gameLoop.start();

    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        powerPellets = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') { //block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') { //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //pacman
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == 'Q') { //power pellet
                    Block powerPellet = new Block(powerFoodImage, x + 8, y + 8, tileSize/2, tileSize/2);
                    powerPellets.add(powerPellet);
                }
                else if (tileMapChar == ' ') { //food
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        for (Block powerPellet : powerPellets) {
            g.drawImage(powerPellet.image, powerPellet.x, powerPellet.y,
                    powerPellet.width, powerPellet.height, null);
        }
        //score and game state
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String gameStatus = "x" + lives + " Score: " + score;
        if (powerModeTimeRemaining > 0) {
            int secondsRemaining = (powerModeTimeRemaining + 999)/1000;
            gameStatus += " Power: " + secondsRemaining + "s";
        }
        g.drawString(gameStatus, tileSize/2, tileSize/2);

        if (gameState == GameState.READY) {
            drawCenteredMessage(g, "Press SPACE to start");
        }
        else if (gameState == GameState.PAUSED) {
            drawCenteredMessage(g, "Paused - Press P to resume");
        }
        else if (gameState == GameState.GAME_OVER) {
            drawCenteredMessage(g, "Game Over: " + score + " - Press R to restart");
        }
    }

    private void drawCenteredMessage(Graphics g, String message) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();
        int x = (boardWidth - metrics.stringWidth(message))/2;
        int y = boardHeight/2;
        g.setColor(Color.YELLOW);
        g.drawString(message, x, y);
    }

    public void move() {
        updatePowerModeTimer();
        applyRequestedDirection();

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
        wrapThroughTunnel(pacman);

        //check wall collisions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collisions
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameState = GameState.GAME_OVER;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize*tunnelRow && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            wrapThroughTunnel(ghost);
            for (Block wall : walls) {
                if (collision(ghost, wall) || hitsHorizontalBoundaryOutsideTunnel(ghost)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        Block powerPelletEaten = null;
        for (Block powerPellet : powerPellets) {
            if (collision(pacman, powerPellet)) {
                powerPelletEaten = powerPellet;
                score += 50;
                powerModeTimeRemaining = powerModeDuration;
            }
        }
        powerPellets.remove(powerPelletEaten);

        if (foods.isEmpty() && powerPellets.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    private void updatePowerModeTimer() {
        if (powerModeTimeRemaining > 0) {
            powerModeTimeRemaining = Math.max(0, powerModeTimeRemaining - gameLoop.getDelay());
        }
    }

    private void wrapThroughTunnel(Block block) {
        if (block.y != tunnelRow*tileSize) {
            return;
        }

        if (block.x + block.width <= 0) {
            block.x = boardWidth;
        }
        else if (block.x >= boardWidth) {
            block.x = -block.width;
        }
    }

    private boolean hitsHorizontalBoundaryOutsideTunnel(Block block) {
        return block.y != tunnelRow*tileSize &&
                (block.x <= 0 || block.x + block.width >= boardWidth);
    }

    private void resetGame() {
        loadMap();
        resetPositions();
        lives = 3;
        score = 0;
        gameState = GameState.READY;
    }

    private void applyRequestedDirection() {
        if (requestedDirection == '\0') {
            return;
        }

        int previousX = pacman.x;
        int previousY = pacman.y;
        pacman.updateDirection(requestedDirection);
        pacman.x = previousX;
        pacman.y = previousY;

        if (pacman.direction == requestedDirection) {
            updatePacmanImage();
        }
    }

    private void updatePacmanImage() {
        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        requestedDirection = '\0';
        powerModeTimeRemaining = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            move();
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (gameState == GameState.PLAYING) {
                gameState = GameState.PAUSED;
            }
            else if (gameState == GameState.PAUSED) {
                gameState = GameState.PLAYING;
            }
            return;
        }

        if (gameState == GameState.READY) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameState = GameState.PLAYING;
            }
            return;
        }

        if (gameState != GameState.PLAYING) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            requestedDirection = 'U';
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            requestedDirection = 'D';
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            requestedDirection = 'L';
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            requestedDirection = 'R';
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
