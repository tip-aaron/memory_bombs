package flip_n_match.game.settings;

import java.util.prefs.Preferences;

public record GameplaySettings(SettingsProperty<GameDifficulty> difficulty,
                               SettingsProperty<Boolean> autoFlag) {
    public GameplaySettings(Preferences prefs) {
        this(new SettingsProperty<>(
                        "app.game.difficulty",
                        GameDifficulty.EASY,
                        prefs
                ),
                new SettingsProperty<>(
                        "app.game.autoflag",
                        false,
                        prefs
                ));
    }
}
