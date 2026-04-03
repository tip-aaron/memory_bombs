package flip_n_match.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ABaseAudioPlayer {
    protected final ExecutorService loaderThread = Executors.newCachedThreadPool();

    /**
     *
     * @param resourcePath Relative to this class' package.
     * @return Clip
     * @throws UnsupportedAudioFileException if audio file not supported by native java
     * @throws IOException                   if resourcePath doesn't exist
     * @throws LineUnavailableException      if Clip is not available to open
     */
    protected Clip loadAudio(String resourcePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            assert is != null;

            try (InputStream buffer = new BufferedInputStream(is)) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(buffer);
                AudioFormat format = audioStream.getFormat();
                AudioFormat decoded = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false
                );

                AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decoded, audioStream);
                Clip clip = AudioSystem.getClip();

                clip.open(decodedStream);

                return clip;
            }
        }
    }

    /**
     * Java's FloatControl uses logarithmic Decibels (dB).
     * This helper converts a linear 0.0f - 1.0f volume into standard dB.
     *
     * @param clip   the audio clip/file
     * @param linVol the volume in linear format
     */
    protected void setVolume(Clip clip, float linVol) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        // Clamp linear volume between 0.0001 and 1.0 to prevent Math.log(0) resulting in -Infinity
        linVol = Math.max(0.0001f, Math.min(1.0f, linVol));
        float dB = (float) (Math.log10(linVol) * 20.0);

        // Ensure we don't exceed the system's min/max gain limits
        dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
        gainControl.setValue(dB);
    }

    public abstract void stop();

    public abstract void play(String resPath);
}
