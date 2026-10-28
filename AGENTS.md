# Pac-Man Project Instructions

These instructions apply to the entire repository.

## Project goal

Improve this Java Swing Pac-Man recreation incrementally while keeping the game playable after every change. Work on exactly one approved feature at a time.

## Required feature workflow

Follow these steps for every feature, bug fix, refactor, or gameplay change:

1. Inspect the current source code and `README.md` before proposing work.
2. Describe the current behavior relevant to the requested change.
3. Propose one small, clearly scoped feature or change.
4. Explain:
   - what the player will experience;
   - which files are likely to change;
   - the implementation approach;
   - important design choices or tradeoffs;
   - how the result will be tested.
5. Suggest useful alternatives or follow-up improvements when appropriate.
6. Ask the user to approve the proposal.
7. Do not edit source code, assets, build configuration, or documentation until the user explicitly approves the proposal.
8. After approval, create a dedicated feature branch from an up-to-date `main` branch. Do not implement a feature directly on `main`.
9. Implement only the approved scope on that feature branch. Do not silently add adjacent features.
10. Test the change in proportion to its risk. At minimum, compile the project and check the affected gameplay behavior when the environment permits it.
11. Update `README.md` in the same change:
    - move the feature into the implemented-features section;
    - update controls or gameplay rules if they changed;
    - update setup or run instructions if they changed;
    - record significant technical or structural changes;
    - keep the planned-features section accurate.
12. Commit the completed feature and its documentation on the feature branch.
13. Merge the feature branch into `main` only after the feature is complete and all required verification has passed.
14. Report what changed, how it was verified, the feature branch name, the merge result, and any remaining limitations.
15. Stop and wait for approval before beginning another feature.

## Approval rules

- Discussion, investigation, and read-only diagnostics do not require approval.
- Any code or project-file modification requires approval of a concrete proposal first.
- Approval applies only to the feature and scope described in the latest proposal.
- If implementation reveals a materially different requirement, stop, explain it, and request fresh approval.
- If the user requests several features, split them into an ordered list and seek approval for the first feature only.
- Never treat a general request such as "improve the game" as approval to implement multiple changes.

## Git branch workflow

- Keep `main` in a stable, playable state.
- Before starting an approved feature, update local `main` from `origin/main` and verify that the working tree is clean.
- Create one branch per feature from `main` using the format `codex/feature-<number>-<short-name>`, for example `codex/feature-2-buffered-input`.
- Make all source, asset, test, and README changes for that feature on its feature branch.
- Do not combine separate roadmap features in one branch.
- Compile and run all relevant tests before merging.
- Do not merge a branch with failing tests, compilation errors, known regressions, or incomplete documentation.
- Once the feature is complete and verified, merge the feature branch into `main` without rewriting published history.
- Push the feature branch and updated `main` when the repository workflow calls for remote synchronization.
- If a merge conflict changes the approved implementation materially, stop and request user direction.
- Do not begin the next feature until the current branch has been merged and the user has approved the next proposal.

## Implementation principles

- Preserve existing working behavior unless the approved feature intentionally changes it.
- Prefer small, reviewable changes over large rewrites.
- Keep gameplay logic understandable and use descriptive names.
- Avoid unrelated formatting or refactoring in feature changes.
- Reuse existing artwork where suitable before adding new assets.
- Do not commit generated output such as `.class` files or IDE build directories as part of a feature.
- Keep the Java Swing event-dispatch thread and timer behavior in mind when changing UI or game-loop code.
- Never claim a feature works without stating what verification was actually performed.

## Current project layout

- `pacman2/src/Main.java` creates the Swing window and starts the game.
- `pacman2/src/PacMan.java` currently contains the map, entities, movement, collision detection, rendering, score, lives, and game loop.
- `pacman2/src/*.png` contains the game artwork.
- `pacman2/target/` is generated Maven build output and should not be edited manually.
- `pacman2/pom.xml` is the Maven build configuration.

## Numbered feature roadmap

Implement the following features in order unless the user explicitly approves a different priority. Each numbered item is a separate feature and requires its own proposal and approval before coding begins.

1. **Project cleanup and reliable build — completed**
   - Correct the initial window sizing and Swing startup behavior.
   - Remove duplicate or unused project files where safe.
   - Add `.gitignore` rules for IDE and compiled output.
   - Add a reproducible Maven or Gradle build.
2. **Responsive controls and buffered turns — completed**
   - Read movement input on key press.
   - Remember the player's requested direction and turn at the next valid opening.
3. **Pause, restart, and start states**
   - Add clear start, paused, playing, and game-over states.
   - Provide documented keys for pausing and restarting.
4. **Tunnel wrapping**
   - Allow Pac-Man and ghosts to travel through the matching left and right tunnel entrances.
5. **Power pellets**
   - Place power pellets on the map using the existing artwork.
   - Give them a distinct score value and temporary gameplay effect.
6. **Frightened ghost mode**
   - Change ghosts to the scared sprite while a power pellet is active.
   - Make frightened ghosts vulnerable and restore them when the timer expires.
7. **Ghost-eating bonuses**
   - Allow Pac-Man to eat frightened ghosts.
   - Award increasing points for consecutive ghosts eaten during one power-pellet period.
   - Return eaten ghosts to their starting area.
8. **Improved ghost behavior**
   - Replace purely random direction changes with understandable chase, scatter, and frightened behavior.
   - Keep each ghost's behavior testable and distinct where practical.
9. **Character animation**
   - Animate Pac-Man's mouth and relevant ghost states without changing movement speed.
10. **Cherry and bonus items**
    - Spawn a timed bonus item based on level progress.
    - Use the existing cherry artwork and award a documented score bonus.
11. **Sound effects and music**
    - Add audio for core events such as starting, eating pellets, power mode, losing a life, and game over.
    - Provide a way to mute audio.
12. **Levels and difficulty progression**
    - Track the current level.
    - Increase difficulty in controlled, documented steps after a maze is cleared.
13. **High-score persistence**
    - Display the high score and save it locally between game sessions.
14. **Code structure refactor**
    - Separate game state, map data, entities, input, and rendering into focused classes.
    - Preserve gameplay while restructuring and avoid combining this with a new player-facing feature.
15. **Automated tests**
    - Add tests for collision detection, scoring, map loading, state transitions, and other non-visual rules.

When one item is complete, mark it complete in this roadmap and in `README.md`, then propose the next incomplete item. Do not start the next item automatically.

## Documentation standard

`README.md` is the source of truth for the player-facing project overview. Keep it concise and truthful. Do not list a planned feature as implemented until its code and verification are complete.
