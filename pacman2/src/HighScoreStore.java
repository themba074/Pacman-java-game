import java.util.prefs.Preferences;

interface HighScoreStore {
    int load();

    void save(int highScore);
}

final class PreferencesHighScoreStore implements HighScoreStore {
    private static final String HIGH_SCORE_KEY = "highScore";
    private final Preferences preferences;

    PreferencesHighScoreStore() {
        Preferences availablePreferences;
        try {
            availablePreferences = Preferences.userNodeForPackage(PacMan.class);
        }
        catch (SecurityException exception) {
            availablePreferences = null;
        }
        preferences = availablePreferences;
    }

    @Override
    public int load() {
        if (preferences == null) {
            return 0;
        }
        try {
            return Math.max(0, preferences.getInt(HIGH_SCORE_KEY, 0));
        }
        catch (IllegalStateException | SecurityException exception) {
            return 0;
        }
    }

    @Override
    public void save(int highScore) {
        if (preferences == null) {
            return;
        }
        try {
            preferences.putInt(HIGH_SCORE_KEY, Math.max(0, highScore));
        }
        catch (IllegalStateException | SecurityException exception) {
            // Persistence is optional; gameplay should continue if it is unavailable.
        }
    }
}
