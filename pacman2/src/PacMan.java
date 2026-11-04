import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener {
    private final MazeMap mazeMap = new MazeMap();
    private final int rowCount = MazeMap.ROW_COUNT;
    private final int columnCount = MazeMap.COLUMN_COUNT;
    private final int tileSize = MazeMap.TILE_SIZE;
    private final int boardWidth = MazeMap.BOARD_WIDTH;
    private final int boardHeight = MazeMap.BOARD_HEIGHT;
    private final int tunnelRow = MazeMap.TUNNEL_ROW;

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

    HashSet<GameEntity> walls;
    HashSet<GameEntity> foods;
    HashSet<GameEntity> powerPellets;
    HashSet<GameEntity> ghosts;
    GameEntity pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    char requestedDirection = '\0';
    GameSession session;
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
    private int ghostMovementFrame = 0;
    private static final int GHOST_MOVEMENT_CYCLE = 5;
    boolean pacmanMouthOpen = true;
    int pacmanAnimationTime = 0;
    private int pacmanAnimationFrameDuration = 150;
    boolean frightenedGhostsShowScared = true;
    int frightenedFlashTime = 0;
    private int frightenedFlashThreshold = 2000;
    private int frightenedFlashFrameDuration = 200;
    GameEntity bonusItem;
    int bonusItemTimeRemaining = 0;
    int bonusItemScore = 0;
    int pelletsEatenThisLevel = 0;
    boolean firstBonusSpawned = false;
    boolean secondBonusSpawned = false;
    private int bonusItemDuration = 10000;
    private int firstBonusThreshold = 70;
    private int secondBonusThreshold = 170;
    SoundManager soundManager;
    HighScoreStore highScoreStore;
    GameRenderer renderer;

    PacMan() {
        this(new SoundManager(), new PreferencesHighScoreStore());
    }

    PacMan(SoundManager soundManager) {
        this(soundManager, new PreferencesHighScoreStore());
    }

    PacMan(SoundManager soundManager, HighScoreStore highScoreStore) {
        this.soundManager = soundManager;
        this.highScoreStore = highScoreStore;
        session = new GameSession(loadHighScore());
        renderer = new GameRenderer();
        applyLevelDifficulty();
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(new InputController(this::handleInput));
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
        for (GameEntity ghost : ghosts) {
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
        walls = new HashSet<GameEntity>();
        foods = new HashSet<GameEntity>();
        powerPellets = new HashSet<GameEntity>();
        ghosts = new HashSet<GameEntity>();
        pelletsEatenThisLevel = 0;
        firstBonusSpawned = false;
        secondBonusSpawned = false;
        clearBonusItem();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = mazeMap.tileAt(r, c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') { //block wall
                    GameEntity wall = new GameEntity(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') { //blue ghost
                    GameEntity ghost = new GameEntity(blueGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = GhostType.BLUE;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //orange ghost
                    GameEntity ghost = new GameEntity(orangeGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = GhostType.ORANGE;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //pink ghost
                    GameEntity ghost = new GameEntity(pinkGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = GhostType.PINK;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    GameEntity ghost = new GameEntity(redGhostImage, x, y, tileSize, tileSize);
                    ghost.ghostType = GhostType.RED;
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //pacman
                    pacman = new GameEntity(pacmanRightImage, x, y, tileSize, tileSize);
                    pacman.direction = 'R';
                }
                else if (tileMapChar == 'Q') { //power pellet
                    GameEntity powerPellet = new GameEntity(powerFoodImage, x + 8, y + 8, tileSize/2, tileSize/2);
                    powerPellets.add(powerPellet);
                }
                else if (tileMapChar == ' ') { //food
                    GameEntity food = new GameEntity(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw(g, pacman, ghosts, walls, foods, powerPellets, bonusItem,
                session, powerModeTimeRemaining, soundManager.isMuted());
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
        for (GameEntity wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collisions
        boolean moveGhostsThisFrame = shouldMoveGhosts();
        for (GameEntity ghost : ghosts) {
            if (collision(ghost, pacman)) {
                if (ghost.frightened) {
                    eatGhost(ghost);
                    continue;
                }
                else if (!ghost.eatenDuringPowerMode || powerModeTimeRemaining == 0) {
                    session.lives -= 1;
                    if (session.lives == 0) {
                        session.state = GameState.GAME_OVER;
                        soundManager.playEffect(SoundManager.SoundEffect.GAME_OVER);
                        soundManager.setBackgroundPlaying(false);
                        return;
                    }
                    soundManager.playEffect(SoundManager.SoundEffect.LIFE_LOST);
                    resetPositions();
                }
            }

            if (!moveGhostsThisFrame) {
                continue;
            }

            updateGhostDirectionAtIntersection(ghost);
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            wrapThroughTunnel(ghost);
            for (GameEntity wall : walls) {
                if (collision(ghost, wall) || hitsHorizontalBoundaryOutsideTunnel(ghost)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    setGhostDirection(ghost, chooseGhostDirection(ghost));
                    break;
                }
            }
        }

        //check food collision
        GameEntity foodEaten = null;
        for (GameEntity food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                addScore(10);
            }
        }
        foods.remove(foodEaten);
        if (foodEaten != null) {
            soundManager.playEffect(SoundManager.SoundEffect.PELLET);
            recordPelletCollected();
        }

        GameEntity powerPelletEaten = null;
        for (GameEntity powerPellet : powerPellets) {
            if (collision(pacman, powerPellet)) {
                powerPelletEaten = powerPellet;
                addScore(50);
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
            addScore(bonusItemScore);
            soundManager.playEffect(SoundManager.SoundEffect.BONUS);
            clearBonusItem();
        }

        if (foods.isEmpty() && powerPellets.isEmpty()) {
            soundManager.playEffect(SoundManager.SoundEffect.LEVEL_COMPLETE);
            session.level += 1;
            applyLevelDifficulty();
            loadMap();
            resetPositions();
        }
    }

    private void applyLevelDifficulty() {
        int completedLevels = Math.max(0, session.level - 1);
        powerModeDuration = Math.max(minimumPowerModeDuration, 7000 - completedLevels*500);
        scatterModeDuration = Math.max(minimumScatterModeDuration, 7000 - completedLevels*500);
        chaseModeDuration = Math.min(maximumChaseModeDuration, 20000 + completedLevels*1000);
    }

    private void addScore(int points) {
        session.score += points;
        if (session.score > session.highScore) {
            session.highScore = session.score;
            try {
                highScoreStore.save(session.highScore);
            }
            catch (RuntimeException exception) {
                // Keep the session high score when local persistence is unavailable.
            }
        }
    }

    private int loadHighScore() {
        try {
            return Math.max(0, highScoreStore.load());
        }
        catch (RuntimeException exception) {
            return 0;
        }
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
        bonusItem = new GameEntity(image, tileSize*9, tileSize*11, tileSize, tileSize);
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

    private boolean shouldMoveGhosts() {
        ghostMovementFrame = (ghostMovementFrame + 1)%GHOST_MOVEMENT_CYCLE;
        return ghostMovementFrame != 0;
    }

    private void updateGhostDirectionAtIntersection(GameEntity ghost) {
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

    private char chooseGhostDirection(GameEntity ghost) {
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

    private List<Character> getValidGhostDirections(GameEntity ghost) {
        char[] directionPriority = {'U', 'L', 'D', 'R'};
        List<Character> validDirections = new ArrayList<>();
        for (char direction : directionPriority) {
            if (canMove(ghost, direction)) {
                validDirections.add(direction);
            }
        }
        return validDirections;
    }

    private boolean canMove(GameEntity block, char direction) {
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
            for (GameEntity wall : walls) {
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

    private int[] getGhostTarget(GameEntity ghost) {
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
            GameEntity redGhost = findGhost(GhostType.RED);
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

    private GameEntity findGhost(GhostType ghostType) {
        for (GameEntity ghost : ghosts) {
            if (ghost.ghostType == ghostType) {
                return ghost;
            }
        }
        return null;
    }

    private boolean isTileAligned(GameEntity block) {
        return block.x%tileSize == 0 && block.y%tileSize == 0;
    }

    private void setGhostDirection(GameEntity ghost, char direction) {
        ghost.updateDirection(direction);
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
        for (GameEntity ghost : ghosts) {
            ghost.frightened = frightened;
            ghost.eatenDuringPowerMode = false;
            ghost.image = frightened ? scaredGhostImage : ghost.normalImage;
        }
    }

    private void eatGhost(GameEntity ghost) {
        int bonusMultiplier = 1 << Math.min(ghostsEatenThisPowerMode, 3);
        addScore(200*bonusMultiplier);
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

        for (GameEntity ghost : ghosts) {
            if (ghost.frightened) {
                ghost.image = frightenedGhostsShowScared ? scaredGhostImage : ghost.normalImage;
            }
        }
    }

    private void wrapThroughTunnel(GameEntity block) {
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

    private boolean hitsHorizontalBoundaryOutsideTunnel(GameEntity block) {
        return block.y != tunnelRow*tileSize &&
                (block.x <= 0 || block.x + block.width >= boardWidth);
    }

    private void resetGame() {
        soundManager.setBackgroundPlaying(false);
        session.level = 1;
        applyLevelDifficulty();
        loadMap();
        resetPositions();
        session.resetRun();
    }

    private void applyRequestedDirection() {
        if (requestedDirection == '\0') {
            return;
        }

        int previousX = pacman.x;
        int previousY = pacman.y;
        char previousDirection = pacman.direction;
        pacman.updateDirection(requestedDirection);
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
        for (GameEntity wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x = previousX;
                pacman.y = previousY;
                pacman.updateDirection(previousDirection);
                return;
            }
        }
        pacman.x = previousX;
        pacman.y = previousY;
        updatePacmanImage();
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

    public boolean collision(GameEntity a, GameEntity b) {
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
        ghostMovementFrame = 0;
        for (GameEntity ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            setGhostDirection(ghost, newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (session.state == GameState.PLAYING) {
            int previousPacmanX = pacman.x;
            int previousPacmanY = pacman.y;
            move();
            boolean pacmanMoved = pacman.x != previousPacmanX || pacman.y != previousPacmanY;
            updateCharacterAnimations(pacmanMoved);
        }
        repaint();
    }

    private void handleInput(InputController.Command command) {
        if (command == InputController.Command.TOGGLE_MUTE) {
            soundManager.toggleMute();
            return;
        }

        if (command == InputController.Command.RESTART) {
            resetGame();
            return;
        }

        if (command == InputController.Command.PAUSE) {
            if (session.state == GameState.PLAYING) {
                session.state = GameState.PAUSED;
                soundManager.setBackgroundPlaying(false);
            }
            else if (session.state == GameState.PAUSED) {
                session.state = GameState.PLAYING;
                soundManager.setBackgroundPlaying(true);
            }
            return;
        }

        if (session.state == GameState.READY) {
            if (command == InputController.Command.START) {
                session.state = GameState.PLAYING;
                soundManager.playEffect(SoundManager.SoundEffect.START);
                soundManager.setBackgroundPlaying(true);
            }
            return;
        }

        if (session.state != GameState.PLAYING) {
            return;
        }

        if (command == InputController.Command.MOVE_UP) {
            requestedDirection = 'U';
        }
        else if (command == InputController.Command.MOVE_DOWN) {
            requestedDirection = 'D';
        }
        else if (command == InputController.Command.MOVE_LEFT) {
            requestedDirection = 'L';
        }
        else if (command == InputController.Command.MOVE_RIGHT) {
            requestedDirection = 'R';
        }
    }
}
