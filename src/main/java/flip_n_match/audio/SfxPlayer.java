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
                // 3. Stop the previously playing sound immediately
                if (currentClip != null && currentClip.isRunning()) {
                    currentClip.stop();
                }

                // 4. Fetch the sound from memory, or load it if it's the first time
                Clip clip = clipCache.get(resPath);
                if (clip == null) {
                    clip = loadAudio(resPath);
                    clipCache.put(resPath, clip);

                    // NOTE: I completely removed the LineListener that called clip.close()!
                    // If you close() a clip, it is destroyed and must be re-decoded from disk.
                    // By keeping it open in the cache, it plays instantly next time.
                }

                // 5. Rewind the sound to the very beginning
                clip.setFramePosition(0);

                setVolume(clip, globalSfxVolume);

                // 6. Play and track it
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