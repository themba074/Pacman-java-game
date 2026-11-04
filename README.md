# Java Swing Pac-Man

A desktop Pac-Man recreation built with Java Swing. The project currently provides a playable maze with Pac-Man, four ghosts, food pellets, scoring, lives, collision handling, and automatic level reset.

The game is being improved incrementally. Each feature is proposed, reviewed, and approved before it is implemented.

## Current features

- A 19-column by 21-row tile-based maze
- Pac-Man movement in four directions
- Four ghost characters with randomized movement
- Wall collision detection
- Food pellets worth 10 points each
- Three lives
- Score and lives displayed in the game window
- Game-over state and restart on keyboard input
- A new board after all food pellets are collected
- Consistent Maven compilation and launch commands
- Swing window creation on the event-dispatch thread
- Responsive key-press controls with buffered turns at the next valid opening
- Ready, playing, paused, and game-over states with explicit controls
- Left and right tunnel wrapping for Pac-Man and ghosts
- Four power pellets worth 50 points with a seven-second power-mode timer
- Frightened ghosts that become harmless during power mode
- Consecutive frightened-ghost bonuses worth 200, 400, 800, and 1,600 points
- Distinct ghost targeting with alternating scatter and chase modes
- Ghost movement balanced to 80% of Pac-Man's speed
- Animated Pac-Man movement and a flashing frightened-mode expiry warning
- Timed cherry bonuses tied to pellet collection progress
- Original synthesized sound effects and looping background music
- Persistent levels with progressively shorter power/scatter modes and longer chase modes
- A persistent high score displayed in the HUD and retained between game sessions
- Focused classes for session state, maze data, entities, input, and rendering
- A headless JUnit test suite for core non-visual gameplay rules

## Controls

Use the arrow keys to change Pac-Man's direction:

- Up Arrow: move up
- Down Arrow: move down
- Left Arrow: move left
- Right Arrow: move right

Additional controls:

- Space: start the game from the ready screen
- P: pause or resume active gameplay
- R: reset the board, score, and lives, then return to the ready screen
- M: mute or unmute all audio

Arrow-key input is captured as soon as the key is pressed. If a requested turn is blocked, Pac-Man keeps moving in the current direction and performs the turn automatically at the next valid opening.

After game over, press R to reset the game, then press Space to start.

## Power pellets

Four larger pellets appear near the maze corners. Each one awards 50 points and activates a seven-second power-mode countdown shown in the HUD. Power mode pauses with the game. During power mode, ghosts use the scared sprite and can be eaten on collision. Consecutive ghosts are worth 200, 400, 800, and 1,600 points. Eaten ghosts return to their starting area and cannot be scored again during the same power period. Their normal danger returns when the timer expires.

## Ghost behavior

Ghosts alternate between seven-second scatter periods and 20-second chase periods. Red targets Pac-Man directly, Pink aims ahead of Pac-Man, Blue calculates an ambush target using Red's position, and Orange chases from a distance but retreats to its corner when close. At intersections, ghosts select legal routes without reversing unless no alternative exists. Ghosts move at 80% of Pac-Man's speed to keep their predictive behavior approachable. Frightened ghosts choose randomly from the same legal routes. The chase/scatter timer pauses during power mode and while the game is paused.

## Character animation

Pac-Man alternates between the directional open-mouth sprite and a closed-mouth frame every 150 milliseconds while moving. The mouth stays open while stationary. During the final two seconds of power mode, frightened ghosts flash every 200 milliseconds as an expiry warning. Animation freezes while the game is paused and does not affect movement speed or collision state.

## Bonus items

A 100-point cherry appears below the ghost house after 70 pellets are collected. A second bonus using the alternate cherry artwork appears after 170 pellets and is worth 300 points. Each bonus remains for ten seconds, and each can spawn only once per level. The timer freezes while paused. Collection, expiry, restart, or level completion clears the active bonus as appropriate.

## Audio

The game generates original synthetic sound effects at runtime for starting, pellets, power pellets, bonus items, eaten ghosts, lost lives, game over, and level completion. A generated background pattern loops only during active gameplay and stops on pause, ready, and game-over screens. Muting preserves the requested gameplay-music state so it resumes when unmuted. If no Java audio line is available, audio disables itself without interrupting gameplay.

## Levels and difficulty

Clearing every regular and power pellet advances to the next level while preserving the current score and remaining lives. The HUD displays the level. Each new level shortens power mode and scatter mode by 500 milliseconds, down to a three-second minimum, and extends chase mode by one second, up to a 30-second maximum. Movement speed remains unchanged to preserve tile alignment. Pressing R starts a new run at Level 1.

## High score

The HUD displays the highest score achieved. The record is saved in the current user's Java preferences and loaded the next time the game starts. Pressing R resets the current run but does not erase the high score. If local preference storage is unavailable, the game remains playable and keeps the record for the current session.

## Technology

- Java
- Java Swing and AWT
- Maven
- JUnit 5
- IntelliJ IDEA project structure
- PNG sprite assets

## Project structure

```text
pacman2/
|-- src/
|   |-- Main.java       # Creates the window and starts the game
|   |-- PacMan.java     # Coordinates gameplay and the Swing game loop
|   |-- GameEntity.java # Movable and drawable entity state
|   |-- GameRenderer.java # Board, HUD, and state-message rendering
|   |-- GameSession.java # Score, lives, level, and play state
|   |-- InputController.java # Keyboard-to-command mapping
|   |-- MazeMap.java    # Maze layout and board dimensions
|   |-- HighScoreStore.java # Local high-score persistence
|   |-- SoundManager.java # Runtime-generated effects, music, and mute state
|   `-- *.png           # Character, wall, and item artwork
|-- test/               # Headless JUnit tests for non-visual game rules
`-- pom.xml             # Maven build and run configuration
```

Repository-level files include `AGENTS.md` for the development workflow, `README.md` for project documentation, and `.gitignore` for generated-file exclusions.

## Running the game

### Maven

From `pacman2`, compile the project with:

```text
mvn clean compile
```

Run the game with:

```text
mvn exec:java
```

Run the automated tests with:

```text
mvn test
```

The tests run in headless mode with audio output disabled and in-memory high-score storage where gameplay integration is exercised.

Maven requires Java 11 or newer.

### IntelliJ IDEA

1. Open `pacman2` as a project or module.
2. Make sure a Java SDK is configured.
3. Run the `Main` class in `src/Main.java`.

The image files must remain available on the runtime classpath alongside the compiled classes.

## Feature roadmap

Features will be proposed, approved, and implemented one at a time. The default order is:

1. [x] Project cleanup and reliable build
2. [x] Responsive controls and buffered turns
3. [x] Pause, restart, and start states
4. [x] Tunnel wrapping
5. [x] Power pellets
6. [x] Frightened ghost mode
7. [x] Ghost-eating bonuses
8. [x] Improved ghost behavior
9. [x] Character animation
10. [x] Cherry and bonus items
11. [x] Sound effects and music
12. [x] Levels and difficulty progression
13. [x] High-score persistence
14. [x] Code structure refactor
15. [x] Automated tests

All 15 roadmap features are complete.

## Development workflow

Before code is changed, the proposed feature must be described with its scope, implementation approach, tradeoffs, and test plan. The user must explicitly approve that proposal. Only one approved feature is implemented at a time.

Every completed feature must include a corresponding update to this README so that the documented features, controls, structure, and plans remain accurate.

Each approved feature is developed on its own branch created from `main`, using a name such as `codex/feature-2-buffered-input`. The feature and its documentation are tested and committed on that branch. It is merged into `main` only after compilation and all relevant verification pass, keeping `main` stable and playable.
