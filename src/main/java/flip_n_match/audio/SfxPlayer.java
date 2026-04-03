package flip_n_match.audio;

import javax.sound.sampled.Clip;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SfxPlayer extends ABaseAudioPlayer {

    // 1. Cache to store loaded sounds in memory (fixes the delay!)
    private final Map<String, Clip> clipCache = new HashMap<>();
    private float globalSfxVolume = 1.0f;
    // 2. Track the active sound so we can interrupt it
    private Clip currentClip;

    public void setVolume(float volume) {
        this.globalSfxVolume = volume;

        if (currentClip != null) {
            setVolume(currentClip, volume);
        }
    }

    @Override
    public void play(String resPath) {
        loaderThread.submit(() -> {
            try {
                if (currentClip != null && currentClip.isRunning()) {
                    currentClip.stop();
                }

                Clip clip = clipCache.get(resPath);

                if (clip == null) {
                    clip = loadAudio(resPath);
                    clipCache.put(resPath, clip);
                }

                clip.setFramePosition(0);
                setVolume(clip, globalSfxVolume);
                clip.start();

                currentClip = clip;
            } catch (Exception e) {
                System.err.println("Failed to play SFX: " + resPath);
                Logger.getLogger(SfxPlayer.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }

    @Override
    public void stop() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
        }
    }

    // Optional: Call this when your game first launches to eliminate the
    // tiny delay on the very FIRST click of the game.
    public void preload(String resPath) {
        loaderThread.submit(() -> {
            if (!clipCache.containsKey(resPath)) {
                try {
                    Clip clip = loadAudio(resPath);
                    clipCache.put(resPath, clip);
                } catch (Exception e) {
                    System.err.println("Failed to preload SFX: " + resPath);
                }
            }
        });
    }
}