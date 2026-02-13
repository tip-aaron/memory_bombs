package flip_n_match.game;

import java.util.function.Consumer;

import flip_n_match.lib.Stopwatch;

public class GameState {
    private final Stopwatch stopwatch;
    private String elapsedTime = "00:00.000";

    private int[][] board;

    private final Consumer<String> stopwatchListener = new Consumer<String>() {
        public void accept(final String time) {
            elapsedTime = time;
        };
    };

    public GameState() {
        this.stopwatch = new Stopwatch(stopwatchListener);
    }

}
