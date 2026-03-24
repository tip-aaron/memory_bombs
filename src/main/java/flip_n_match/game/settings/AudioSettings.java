package flip_n_match.game.settings;

import java.util.prefs.Preferences;

public record AudioSettings(SettingsProperty<Integer> masterVolume) {
    public AudioSettings(Preferences masterVolume) {
        this(new SettingsProperty<>(
                "app.audio.volume.master",
                75,
                masterVolume
        ));
    }
}
