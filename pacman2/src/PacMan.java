import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    enum GameState {
        READY,
        PLAYING,
        PAUSED,
        GAME_OVER
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

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        Image normalImage;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;
        boolean frightened = false;
        boolean eatenDuringPowerMode = false;
        GhostType ghostType;

        Block(Image image, int x, int y, int width, int height) {
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
    private Image scaredGhostImage;
    private Image powerFoodImage;
    private Image cherryImage;
    private Image cherry2Image;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    private Image pacmanClosedImage;

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
    int ghostsEatenThisPowerMode = 0;
    private int powerModeDuration = 7000;
    private int minimumPowerModeDuration = 3000;
    GhostMode ghostMode = GhostMode.SCATTER;
    int ghostModeTimeRemaining = 7000;
    private int scatterModeDuration = 7000;
    private int minimumScatterModeDuration = 3000;
    private int chaseModeDuration = 20000;
    private int maximumChaseModeDuration = 30000;
    int level = 1;
    boolean pacmanMouthOpen = true;
    int pacmanAnimationTime = 0;
    private int pacmanAnimationFrameDuration = 150;
    boolean frightenedGhostsShowScared = true;
    int frightenedFlashTime = 0;
    private int frightenedFlashThreshold = 2000;
    private int frightenedFlashFrameDuration = 200;
    Block bonusItem;
    int bonusItemTimeRemaining = 0;
    int bonusItemScore = 0;
    int pelletsEatenThisLevel = 0;
    boolean firstBonusSpawned = false;
    boolean secondBonusSpawned = false;
    private int bonusItemDuration = 10000;
    private int firstBonusThreshold = 70;
    private int secondBonusThreshold = 170;
    SoundManager soundManager;

    PacMan() {
        this(new SoundManager());
    }

    PacMan(SoundManager soundManager) {
        this.soundManager = soundManager;
        applyLevelDifficulty();
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
        scaredGhostImage = new ImageIcon(getClass().getResource("./scaredGhost.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("./powerFood.png")).getImage();
        cherryImage = new ImageIcon(getClass().getResource("./cherry.png")).getImage();
        cherry2Image = new ImageIcon(getClass().getResource("./cherry2.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        pacmanClosedImage = createPacmanClosedImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            setGhostDirection(ghost, newDirection);
        }
        //how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); //20fps (1000/50)
        gameLoop.start();

    }

    private Image createPacmanClosedImage() {
        BufferedImage image = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.YELLOW);
        graphics.fillOval(2, 2, tileSize - 4, tileSize - 4);
        graphics.dispose();
        return image;
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        powerPellets = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        pelletsEatenThisLevel = 0;
        firstBonusSpawned = false;
        secondBonusSpawned = false;
        clearBonusItem();

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
                    ghost.ghostType = GhostType.BLUE;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = GhostType.ORANGE;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = GhostType.PINK;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = GhostType.RED;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //pacman
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    pacman.direction = 'R';
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
        if (bonusItem != null) {
            g.drawImage(bonusItem.image, bonusItem.x, bonusItem.y,
                    bonusItem.width, bonusItem.height, null);
        }
        //score and game state
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String gameStatus = "Level " + level + "  x" + lives + " Score: " + score;
        if (powerModeTimeRemaining > 0) {
            int secondsRemaining = (powerModeTimeRemaining + 999)/1000;
            gameStatus += " Power: " + secondsRemaining + "s";
        }
        if (soundManager.isMuted()) {
            gameStatus += " Muted";
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
        updateGhostModeTimer();
        updateBonusItemTimer();
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
                if (ghost.frightened) {
                    eatGhost(ghost);
                    continue;
                }
                else if (!ghost.eatenDuringPowerMode || powerModeTimeRemaining == 0) {
                    lives -= 1;
                    if (lives == 0) {
                        gameState = GameState.GAME_OVER;
                        soundManager.playEffect(SoundManager.SoundEffect.GAME_OVER);
                        soundManager.setBackgroundPlaying(false);
                        return;
                    }
                    soundManager.playEffect(SoundManager.SoundEffect.LIFE_LOST);
                    resetPositions();
                }
            }

            updateGhostDirectionAtIntersection(ghost);
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            wrapThroughTunnel(ghost);
            for (Block wall : walls) {
                if (collision(ghost, wall) || hitsHorizontalBoundaryOutsideTunnel(ghost)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    setGhostDirection(ghost, chooseGhostDirection(ghost));
                    break;
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
        if (foodEaten != null) {
            soundManager.playEffect(SoundManager.SoundEffect.PELLET);
            recordPelletCollected();
        }

        Block powerPelletEaten = null;
        for (Block powerPellet : powerPellets) {
            if (collision(pacman, powerPellet)) {
                powerPelletEaten = powerPellet;
                score += 50;
                powerModeTimeRemaining = powerModeDuration;
                setGhostsFrightened(true);
            }
        }
        powerPellets.remove(powerPelletEaten);
        if (powerPelletEaten != null) {
            soundManager.playEffect(SoundManager.SoundEffect.POWER_PELLET);
            recordPelletCollected();
        }

        if (bonusItem != null && collision(pacman, bonusItem)) {
            score += bonusItemScore;
            soundManager.playEffect(SoundManager.SoundEffect.BONUS);
            clearBonusItem();
        }

        if (foods.isEmpty() && powerPellets.isEmpty()) {
            soundManager.playEffect(SoundManager.SoundEffect.LEVEL_COMPLETE);
            level += 1;
            applyLevelDifficulty();
            loadMap();
            resetPositions();
        }
    }

    private void applyLevelDifficulty() {
        int completedLevels = Math.max(0, level - 1);
        powerModeDuration = Math.max(minimumPowerModeDuration, 7000 - completedLevels*500);
        scatterModeDuration = Math.max(minimumScatterModeDuration, 7000 - completedLevels*500);
        chaseModeDuration = Math.min(maximumChaseModeDuration, 20000 + completedLevels*1000);
    }

    private void updateBonusItemTimer() {
        if (bonusItem == null) {
            return;
        }

        bonusItemTimeRemaining = Math.max(0, bonusItemTimeRemaining - gameLoop.getDelay());
        if (bonusItemTimeRemaining == 0) {
            clearBonusItem();
        }
    }

    private void recordPelletCollected() {
        pelletsEatenThisLevel += 1;
        if (!secondBonusSpawned && pelletsEatenThisLevel >= secondBonusThreshold) {
            secondBonusSpawned = true;
            spawnBonusItem(cherry2Image, 300);
        }
        else if (!firstBonusSpawned && pelletsEatenThisLevel >= firstBonusThreshold) {
            firstBonusSpawned = true;
            spawnBonusItem(cherryImage, 100);
        }
    }

    private void spawnBonusItem(Image image, int scoreValue) {
        bonusItem = new Block(image, tileSize*9, tileSize*11, tileSize, tileSize);
        bonusItemScore = scoreValue;
        bonusItemTimeRemaining = bonusItemDuration;
    }

    private void clearBonusItem() {
        bonusItem = null;
        bonusItemScore = 0;
        bonusItemTimeRemaining = 0;
    }

    private void updatePowerModeTimer() {
        if (powerModeTimeRemaining > 0) {
            powerModeTimeRemaining = Math.max(0, powerModeTimeRemaining - gameLoop.getDelay());
            if (powerModeTimeRemaining == 0) {
                setGhostsFrightened(false);
            }
        }
    }

    private void updateGhostModeTimer() {
        if (powerModeTimeRemaining > 0) {
            return;
        }

        ghostModeTimeRemaining -= gameLoop.getDelay();
        if (ghostModeTimeRemaining <= 0) {
            if (ghostMode == GhostMode.SCATTER) {
                ghostMode = GhostMode.CHASE;
                ghostModeTimeRemaining = chaseModeDuration;
            }
            else {
                ghostMode = GhostMode.SCATTER;
                ghostModeTimeRemaining = scatterModeDuration;
            }
        }
    }

    private void updateGhostDirectionAtIntersection(Block ghost) {
        if (!isTileAligned(ghost)) {
            return;
        }

        boolean insideGhostHouse = ghost.y == tileSize*tunnelRow &&
                ghost.x >= tileSize*8 && ghost.x <= tileSize*10;
        boolean movingHorizontally = ghost.direction == 'L' || ghost.direction == 'R';
        if (insideGhostHouse && movingHorizontally && canMove(ghost, 'U')) {
            setGhostDirection(ghost, 'U');
            return;
        }

        setGhostDirection(ghost, chooseGhostDirection(ghost));
    }

    private char chooseGhostDirection(Block ghost) {
        List<Character> validDirections = getValidGhostDirections(ghost);
        if (validDirections.isEmpty()) {
            return ghost.direction;
        }

        char reverseDirection = reverseDirection(ghost.direction);
        if (validDirections.size() > 1) {
            validDirections.remove(Character.valueOf(reverseDirection));
        }

        if (ghost.frightened) {
            return validDirections.get(random.nextInt(validDirections.size()));
        }

        int[] target = getGhostTarget(ghost);
        char bestDirection = validDirections.get(0);
        long bestDistance = Long.MAX_VALUE;
        for (char direction : validDirections) {
            int nextX = ghost.x + directionDeltaX(direction);
            int nextY = ghost.y + directionDeltaY(direction);
            long deltaX = target[0] - nextX;
            long deltaY = target[1] - nextY;
            long distance = deltaX*deltaX + deltaY*deltaY;
            if (distance < bestDistance) {
                bestDistance = distance;
                bestDirection = direction;
            }
        }
        return bestDirection;
    }

    private List<Character> getValidGhostDirections(Block ghost) {
        char[] directionPriority = {'U', 'L', 'D', 'R'};
        List<Character> validDirections = new ArrayList<>();
        for (char direction : directionPriority) {
            if (canMove(ghost, direction)) {
                validDirections.add(direction);
            }
        }
        return validDirections;
    }

    private boolean canMove(Block block, char direction) {
        boolean outsideTunnel = block.x < 0 || block.x + block.width > boardWidth;
        if (outsideTunnel && direction != 'L' && direction != 'R') {
            return false;
        }

        int previousX = block.x;
        int previousY = block.y;
        block.x += directionDeltaX(direction);
        block.y += directionDeltaY(direction);
        wrapThroughTunnel(block);

        boolean blocked = hitsHorizontalBoundaryOutsideTunnel(block);
        if (!blocked) {
            for (Block wall : walls) {
                if (collision(block, wall)) {
                    blocked = true;
                    break;
                }
            }
        }

        block.x = previousX;
        block.y = previousY;
        return !blocked;
    }

    private int[] getGhostTarget(Block ghost) {
        if (ghostMode == GhostMode.SCATTER) {
            return getScatterTarget(ghost.ghostType);
        }

        if (ghost.ghostType == GhostType.RED) {
            return new int[] {pacman.x, pacman.y};
        }
        if (ghost.ghostType == GhostType.PINK) {
            return getPacmanAheadTarget(4);
        }
        if (ghost.ghostType == GhostType.BLUE) {
            int[] aheadTarget = getPacmanAheadTarget(2);
            Block redGhost = findGhost(GhostType.RED);
            if (redGhost == null) {
                return aheadTarget;
            }
            return new int[] {
                    aheadTarget[0] + (aheadTarget[0] - redGhost.x),
                    aheadTarget[1] + (aheadTarget[1] - redGhost.y)
            };
        }

        long deltaX = pacman.x - ghost.x;
        long deltaY = pacman.y - ghost.y;
        long chaseDistance = 8L*tileSize;
        if (deltaX*deltaX + deltaY*deltaY > chaseDistance*chaseDistance) {
            return new int[] {pacman.x, pacman.y};
        }
        return getScatterTarget(GhostType.ORANGE);
    }

    private int[] getScatterTarget(GhostType ghostType) {
        if (ghostType == GhostType.RED) {
            return new int[] {boardWidth, 0};
        }
        if (ghostType == GhostType.PINK) {
            return new int[] {0, 0};
        }
        if (ghostType == GhostType.BLUE) {
            return new int[] {boardWidth, boardHeight};
        }
        return new int[] {0, boardHeight};
    }

    private int[] getPacmanAheadTarget(int tilesAhead) {
        return new int[] {
                pacman.x + directionTileDeltaX(pacman.direction)*tilesAhead,
                pacman.y + directionTileDeltaY(pacman.direction)*tilesAhead
        };
    }

    private Block findGhost(GhostType ghostType) {
        for (Block ghost : ghosts) {
            if (ghost.ghostType == ghostType) {
                return ghost;
            }
        }
        return null;
    }

    private boolean isTileAligned(Block block) {
        return block.x%tileSize == 0 && block.y%tileSize == 0;
    }

    private void setGhostDirection(Block ghost, char direction) {
        ghost.direction = direction;
        ghost.updateVelocity();
    }

    private char reverseDirection(char direction) {
        if (direction == 'U') return 'D';
        if (direction == 'D') return 'U';
        if (direction == 'L') return 'R';
        return 'L';
    }

    private int directionDeltaX(char direction) {
        if (direction == 'L') return -tileSize/4;
        if (direction == 'R') return tileSize/4;
        return 0;
    }

    private int directionDeltaY(char direction) {
        if (direction == 'U') return -tileSize/4;
        if (direction == 'D') return tileSize/4;
        return 0;
    }

    private int directionTileDeltaX(char direction) {
        if (direction == 'L') return -tileSize;
        if (direction == 'R') return tileSize;
        return 0;
    }

    private int directionTileDeltaY(char direction) {
        if (direction == 'U') return -tileSize;
        if (direction == 'D') return tileSize;
        return 0;
    }

    private void setGhostsFrightened(boolean frightened) {
        ghostsEatenThisPowerMode = 0;
        frightenedGhostsShowScared = true;
        frightenedFlashTime = 0;
        for (Block ghost : ghosts) {
            ghost.frightened = frightened;
            ghost.eatenDuringPowerMode = false;
            ghost.image = frightened ? scaredGhostImage : ghost.normalImage;
        }
    }

    private void eatGhost(Block ghost) {
        int bonusMultiplier = 1 << Math.min(ghostsEatenThisPowerMode, 3);
        score += 200*bonusMultiplier;
        ghostsEatenThisPowerMode += 1;
        soundManager.playEffect(SoundManager.SoundEffect.GHOST_EATEN);

        ghost.frightened = false;
        ghost.eatenDuringPowerMode = true;
        ghost.image = ghost.normalImage;
        ghost.reset();
        setGhostDirection(ghost, directions[random.nextInt(4)]);
    }

    private void updateCharacterAnimations(boolean pacmanMoved) {
        updatePacmanAnimation(pacmanMoved);
        updateFrightenedGhostAnimation();
    }

    private void updatePacmanAnimation(boolean pacmanMoved) {
        if (!pacmanMoved) {
            pacmanMouthOpen = true;
            pacmanAnimationTime = 0;
        }
        else {
            pacmanAnimationTime += gameLoop.getDelay();
            if (pacmanAnimationTime >= pacmanAnimationFrameDuration) {
                pacmanAnimationTime -= pacmanAnimationFrameDuration;
                pacmanMouthOpen = !pacmanMouthOpen;
            }
        }
        updatePacmanImage();
    }

    private void updateFrightenedGhostAnimation() {
        if (powerModeTimeRemaining <= 0) {
            return;
        }

        if (powerModeTimeRemaining > frightenedFlashThreshold) {
            frightenedGhostsShowScared = true;
            frightenedFlashTime = 0;
        }
        else {
            frightenedFlashTime += gameLoop.getDelay();
            if (frightenedFlashTime >= frightenedFlashFrameDuration) {
                frightenedFlashTime -= frightenedFlashFrameDuration;
                frightenedGhostsShowScared = !frightenedGhostsShowScared;
            }
        }

        for (Block ghost : ghosts) {
            if (ghost.frightened) {
                ghost.image = frightenedGhostsShowScared ? scaredGhostImage : ghost.normalImage;
            }
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
        soundManager.setBackgroundPlaying(false);
        level = 1;
        applyLevelDifficulty();
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
        if (!pacmanMouthOpen) {
            pacman.image = pacmanClosedImage;
            return;
        }
        pacman.image = getPacmanDirectionalImage();
    }

    private Image getPacmanDirectionalImage() {
        if (pacman.direction == 'U') return pacmanUpImage;
        if (pacman.direction == 'D') return pacmanDownImage;
        if (pacman.direction == 'L') return pacmanLeftImage;
        return pacmanRightImage;
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
        pacmanMouthOpen = true;
        pacmanAnimationTime = 0;
        updatePacmanImage();
        requestedDirection = '\0';
        powerModeTimeRemaining = 0;
        setGhostsFrightened(false);
        ghostMode = GhostMode.SCATTER;
        ghostModeTimeRemaining = scatterModeDuration;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            setGhostDirection(ghost, newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            int previousPacmanX = pacman.x;
            int previousPacmanY = pacman.y;
            move();
            boolean pacmanMoved = pacman.x != previousPacmanX || pacman.y != previousPacmanY;
            updateCharacterAnimations(pacmanMoved);
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_M) {
            soundManager.toggleMute();
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (gameState == GameState.PLAYING) {
                gameState = GameState.PAUSED;
                soundManager.setBackgroundPlaying(false);
            }
            else if (gameState == GameState.PAUSED) {
                gameState = GameState.PLAYING;
                soundManager.setBackgroundPlaying(true);
            }
            return;
        }

        if (gameState == GameState.READY) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameState = GameState.PLAYING;
                soundManager.playEffect(SoundManager.SoundEffect.START);
                soundManager.setBackgroundPlaying(true);
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
