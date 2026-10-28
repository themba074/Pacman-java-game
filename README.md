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

## Controls

Use the arrow keys to change Pac-Man's direction:

- Up Arrow: move up
- Down Arrow: move down
- Left Arrow: move left
- Right Arrow: move right

After game over, release any key to restart. Direction changes currently happen when an arrow key is released.

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
2. Responsive controls and buffered turns
3. Pause, restart, and start states
4. Tunnel wrapping
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

Feature 1 is complete. The remaining items have not been implemented yet. Completed items will be marked here and incorporated into the current-features documentation as development progresses.

## Development workflow

Before code is changed, the proposed feature must be described with its scope, implementation approach, tradeoffs, and test plan. The user must explicitly approve that proposal. Only one approved feature is implemented at a time.

Every completed feature must include a corresponding update to this README so that the documented features, controls, structure, and plans remain accurate.
