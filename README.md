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

Arrow-key input is captured as soon as the key is pressed. If a requested turn is blocked, Pac-Man keeps moving in the current direction and performs the turn automatically at the next valid opening.

After game over, press R to reset the game, then press Space to start.

## Technology

- Java
- Java Swing and AWT
- Maven
- IntelliJ IDEA project structure
- PNG sprite assets

## Project structure

```text
pacman2/
|-- src/
|   |-- Main.java       # Creates the window and starts the game
|   |-- PacMan.java     # Game loop, map, entities, input, and rendering
|   `-- *.png           # Character, wall, and item artwork
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
5. Power pellets
6. Frightened ghost mode
7. Ghost-eating bonuses
8. Improved ghost behavior
9. Character animation
10. Cherry and bonus items
11. Sound effects and music
12. Levels and difficulty progression
13. High-score persistence
14. Code structure refactor
15. Automated tests

Features 1 through 4 are complete. The remaining items have not been implemented yet. Completed items will be marked here and incorporated into the current-features documentation as development progresses.

## Development workflow

Before code is changed, the proposed feature must be described with its scope, implementation approach, tradeoffs, and test plan. The user must explicitly approve that proposal. Only one approved feature is implemented at a time.

Every completed feature must include a corresponding update to this README so that the documented features, controls, structure, and plans remain accurate.

Each approved feature is developed on its own branch created from `main`, using a name such as `codex/feature-2-buffered-input`. The feature and its documentation are tested and committed on that branch. It is merged into `main` only after compilation and all relevant verification pass, keeping `main` stable and playable.
