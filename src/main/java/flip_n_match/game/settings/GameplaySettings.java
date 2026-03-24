package flip_n_match.game.settings;

import java.util.prefs.Preferences;

public record GameplaySettings(SettingsProperty<GameDifficulty> difficulty) {
    public GameplaySettings(Preferences difficulty) {
        this(new SettingsProperty<>(
                "app.game.difficulty",
                GameDifficulty.EASY,
                difficulty
        ));
    }
}
