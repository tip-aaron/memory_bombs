package flip_n_match.game.settings;

import java.util.prefs.Preferences;

public record AppearanceSettings(SettingsProperty<GameTheme> theme) {
    public AppearanceSettings(Preferences theme) {
        this(new SettingsProperty<>("app.appearance.theme", GameTheme.DARK, theme));
    }
}
