package flip_n_match.game.settings;

import lombok.Getter;

import java.util.prefs.Preferences;

public class UserSettings {
    private static UserSettings instance;

    @Getter private final AppearanceSettings appearance;
    @Getter private final GameplaySettings gameplay;
    @Getter private final AudioSettings audio;
    @Getter private final ControlSettings controls;

    private UserSettings() {
        Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);

        appearance = new AppearanceSettings(prefs);
        gameplay = new GameplaySettings(prefs);
        audio = new AudioSettings(prefs);
        controls = new ControlSettings(prefs);
    }

    public static synchronized UserSettings getInstance() {
        if (instance == null) {
            instance = new UserSettings();
        }

        return instance;
    }
}
