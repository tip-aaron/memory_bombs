package flip_n_match.game.settings;

import flip_n_match.audio.AudioManager;

import java.util.prefs.Preferences;


public record AudioSettings(
        SettingsProperty<Integer> masterVolume,
        SettingsProperty<Integer> sfxVolume,
        SettingsProperty<Integer> musicVolume,
        SettingsProperty<Boolean> sfxEnabled,
        SettingsProperty<Boolean> musicEnabled,
        SettingsProperty<AudioManager.Music> selectedMusic,
        SettingsProperty<AudioManager.Sfx> selectedSfx
) {
    public AudioSettings(Preferences prefs) {
        this(
                new SettingsProperty<>("app.audio.volume.master", 20, prefs),
                new SettingsProperty<>("app.audio.volume.sfx", 75, prefs),
                new SettingsProperty<>("app.audio.volume.music", 25, prefs),
                new SettingsProperty<>("app.audio.enabled.sfx", true, prefs),
                new SettingsProperty<>("app.audio.enabled.music", true, prefs),
                new SettingsProperty<>("app.audio.music.track", AudioManager.Music.EIGHT_BIT_NIGHTSHADE, prefs),
                new SettingsProperty<>("app.audio.sfx.track", AudioManager.Sfx.BUTTON_CLICK, prefs)
        );
    }
}