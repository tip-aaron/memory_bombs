package flip_n_match.game.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameEventMessenger {
    private static GameEventMessenger instance;

    private final List<Consumer<Boolean>> gameOverListeners = new ArrayList<>();

    private GameEventMessenger() {}

    public static GameEventMessenger getInstance() {
        if (instance == null) {
            instance = new GameEventMessenger();
        }
        return instance;
    }

    // Subscribe to game over events (true = win, false = loss)
    public void addGameOverListener(Consumer<Boolean> listener) {
        gameOverListeners.add(listener);
    }

    public void removeAllListeners() {
        gameOverListeners.clear();
    }

    // Broadcast the event to all listeners
    public void triggerGameOver(boolean isWin) {
        gameOverListeners.forEach(listener -> listener.accept(isWin));
    }
}