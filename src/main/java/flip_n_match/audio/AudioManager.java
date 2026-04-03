package flip_n_match.audio;

import flip_n_match.game.settings.AudioSettings;
import flip_n_match.game.settings.UserSettings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    private static AudioManager instance;

    private final MusicPlayer musicPlayer;
    private final SfxPlayer sfxPlayer;

    private final List<MusicObserver> musicObservers = new ArrayList<>();

    private AudioManager() {
        musicPlayer = new MusicPlayer();
        sfxPlayer = new SfxPlayer();

        for (Sfx sfx : Sfx.values()) {
            sfxPlayer.preload(sfx.getFileName());
        }

        bindVolumeSettings();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void bindVolumeSettings() {
        AudioSettings audio = UserSettings.getInstance().getAudio();

        updateVolumes();

        audio.masterVolume().addListener((oldVal, newVal) -> updateVolumes());
        audio.sfxVolume().addListener((oldVal, newVal) -> updateVolumes());
        audio.musicVolume().addListener((oldVal, newVal) -> updateVolumes());
        audio.sfxEnabled().addListener((oldVal, newVal) -> updateVolumes());
        audio.musicEnabled().addListener((oldVal, newVal) -> updateVolumes());
    }

    private void updateVolumes() {
        AudioSettings audio = UserSettings.getInstance().getAudio();

        float masterBase = audio.masterVolume().get() / 100.0f;
        float sfxBase = audio.sfxVolume().get() / 100.0f;
        float musicBase = audio.musicVolume().get() / 100.0f;
        float effectiveSfxVolume = audio.sfxEnabled().get() ? (masterBase * sfxBase) : 0.0f;

        sfxPlayer.setVolume(effectiveSfxVolume);

        float effectiveMusicVolume = audio.musicEnabled().get() ? (masterBase * musicBase) : 0.0f;

        musicPlayer.setVolume(effectiveMusicVolume);
    }

    public boolean getIsMusicPlaying() {
        return musicPlayer.getIsMusicPlaying();
    }

    public void addMusicObserver(MusicObserver observer) {
        musicObservers.add(observer);
    }
//
//    public void removeMusicObserver(MusicObserver observer) {
//        musicObservers.remove(observer);
//    }

    private void notifyMusicStarted(Music track) {
        for (MusicObserver obs : musicObservers) {
            obs.onMusicStarted(track);
        }
    }

    private void notifyMusicStopped() {
        for (MusicObserver obs : musicObservers) {
            obs.onMusicStopped();
        }
    }

    private void notifyMusicLoading(Music track) {
        for (MusicObserver obs : musicObservers) {
            obs.onMusicLoading(track);
        }
    }

    public void playMusic(Music track) {
        notifyMusicLoading(track);

        musicPlayer.play(track.getFileName(), () -> notifyMusicStarted(track));
    }

    public void stopMusic() {
        musicPlayer.stop();
        notifyMusicStopped();
    }

    public void setMusicVolume(float volume) {
        musicPlayer.setVolume(volume);
    }

    public void playSfx(Sfx effect) {
        sfxPlayer.play(effect.getFileName());
    }

    public void setSfxVolume(float volume) {
        sfxPlayer.setVolume(volume);
    }

    @Getter
    public enum Sfx {
        BOMB_REVEAL("sfx/bomb_reveal.mp3"),
        TILE_REVEAL("sfx/tile_reveal.mp3"),
        TILE_MATCHING_REVEAL("sfx/tile_matching_reveal.mp3"),
        TILE_MATCHED("sfx/tile_matched.mp3"),
        CHORD_CASCADE("sfx/chord_cascade.mp3"),
        FLAG("sfx/flag.mp3"),
        UNFLAG("sfx/unflag.mp3"),
        BUTTON_HOVER("sfx/button_hover.mp3"),
        BUTTON_CLICK("sfx/button_click.mp3"),
        GAME_OVER("sfx/game_over.mp3"),
        WIN("sfx/win.mp3");

        private final String fileName;

        Sfx(String fileName) {
            this.fileName = fileName;
        }

    }

    @Getter
    public enum Music {
        EDM_FROM_ABOVE("music/edm_From_Above.mp3"),
        SYNTHWAVE_DREAMWALKER("music/synthwave_Dreamwalker.mp3"),
        EIGHT_BIT_NIGHTSHADE("music/8-bit_Nightshade.mp3"),
        EIGHT_BIT_UNDERCOOKED("music/8-bit_Undercooked.mp3"),
        EIGHT_BIT_MAZE("music/8-bit_MAZE.mp3"),
        EIGHT_BIT_A_NIGHT_OF_DIZZY_SPELLS("music/8-bit_A_Night_of_Dizzy_Spells.mp3"),
        EIGHT_BIT_POWERUP("music/8-bit_Powerup.mp3"),
        SOUKAITEI("music/Soukaitei.mp3"),
        MINESWEEPER_PLUS("music/minesweeper_plus.mp3"),
        FINAL_BOSS("music/final_boss.mp3"),
        FINAL_BOSS_2("music/final_boss_2.mp3"),
        FINAL_BOSS_3("music/final_boss_3.mp3"),
        SWEEPING("music/sweeping.mp3");

        private final String fileName;

        Music(String fileName) {
            this.fileName = fileName;
        }

    }

    public interface MusicObserver {
        void onMusicLoading(AudioManager.Music track); // <-- ADD THIS

        void onMusicStarted(AudioManager.Music track);

        void onMusicStopped();
    }
}
