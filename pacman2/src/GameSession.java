final class GameSession {
    int score = 0;
    int highScore;
    int lives = 3;
    int level = 1;
    GameState state = GameState.READY;

    GameSession(int highScore) {
        this.highScore = Math.max(0, highScore);
    }

    void resetRun() {
        score = 0;
        lives = 3;
        level = 1;
        state = GameState.READY;
    }
}

enum GameState {
    READY,
    PLAYING,
    PAUSED,
    GAME_OVER
}
