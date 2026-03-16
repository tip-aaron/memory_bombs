package flip_n_match.lib;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Stopwatch {
    private Thread tickerThread;
    @Getter
    private volatile boolean running = false;
    private final AtomicLong totalNanoSeconds = new AtomicLong(0);

    private long startTimeNano = 0;
    private long accTimeNano = 0;

    private final Consumer<String> onTick;

    public Stopwatch(final Consumer<String> onTick) {
        this.onTick = onTick;
    }

    public synchronized void start() {
        if (running) {
            return;
        }

        running = true;
        startTimeNano = System.nanoTime();

        tickerThread = new Thread(() -> {
            while (running) {
                final long currentDuration = System.nanoTime() - startTimeNano;
                final long totalTime = accTimeNano + currentDuration;
                totalNanoSeconds.set(totalTime);
                final String timeText = formatTime(totalTime);

                onTick.accept(timeText);

                try {
                    Thread.sleep(50);
                } catch (final InterruptedException err) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        tickerThread.setDaemon(true);
        tickerThread.start();
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }

        running = false;
        final long now = System.nanoTime();
        accTimeNano += (now - startTimeNano);

        tickerThread.interrupt();

        onTick.accept(formatTime(accTimeNano));
    }

    public synchronized void reset() {
        stop();
        accTimeNano = 0;
        onTick.accept(formatTime(0));
    }

    private String formatTime(final long nanos) {
        final long totalMillis = nanos / 1_000_000;
        final long minutes = (totalMillis / 60_000);
        final long seconds = (totalMillis) / 1_000 % 60;
        final long millis = totalMillis % 1_000;

        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
}
