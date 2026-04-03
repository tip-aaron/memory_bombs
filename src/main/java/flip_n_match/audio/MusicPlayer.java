package flip_n_match.audio;

import javax.sound.sampled.Clip;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MusicPlayer extends ABaseAudioPlayer {
    private final ScheduledExecutorService faderThread = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, Clip> clipCache = new HashMap<>();
    private Clip currentAudio;
    private Future<?> activeFaderTask;
    private float targetVolume = 0.8f;

    public void setVolume(float volume) {
        this.targetVolume = volume;

        if (currentAudio != null && (activeFaderTask == null || activeFaderTask.isDone())) {
            setVolume(currentAudio, targetVolume);
        }
    }

    public boolean getIsMusicPlaying() {
        return currentAudio != null && currentAudio.isRunning();
    }

    public void play(String resPath, Runnable onStarted) {
        loaderThread.submit(() -> {
            try {
                if (clipCache.containsKey(resPath)) {
                    if (clipCache.get(resPath).equals(currentAudio)) {
                        clipCache.get(resPath).setFramePosition(0);
                        clipCache.get(resPath).start();

                        if (onStarted != null) onStarted.run();
                        return;
                    }
                }

                Clip clip = clipCache.get(resPath);

                if (clip == null) {
                    clip = loadAudio(resPath);
                    clipCache.put(resPath, clip);
                }

                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                setVolume(clip, 0f);
                clip.start();

                crossFade(currentAudio, clip);

                currentAudio = clip;

                if (onStarted != null) {
                    onStarted.run();
                }
            } catch (Exception e) {
                System.err.println("Failed to play Music: " + resPath);
                Logger.getLogger(MusicPlayer.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }

    @Override
    public void play(String resPath) {
        play(resPath, null);
    }

    private void crossFade(Clip oldAudio, Clip newAudio) {
        if (activeFaderTask != null && !activeFaderTask.isDone()) {
            activeFaderTask.cancel(true);

            if (oldAudio != null) {
                oldAudio.stop();
            }
        }

        // 2,500 ms = 50 * 50
        final int fadeSteps = 25;
        final long stepDelayMs = 40;

        activeFaderTask = faderThread.submit(() -> {
            try {
                for (int step = 1; step <= fadeSteps; step++) {
                    float progress = (float) step / fadeSteps;

                    setVolume(newAudio, targetVolume * progress);

                    if (oldAudio != null) {
                        setVolume(oldAudio, targetVolume * (1.0f - progress));
                    }

                    Thread.sleep(stepDelayMs);
                }

                if (oldAudio != null) {
                    oldAudio.stop();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void stop() {
        if (currentAudio != null) {
            if (activeFaderTask != null && !activeFaderTask.isDone()) {
                activeFaderTask.cancel(true);
            }

            System.out.println("STOPPING");

            setVolume(currentAudio, targetVolume);
            currentAudio.stop();
            // REMOVED currentAudio.close()
            currentAudio = null;
        }
    }
}