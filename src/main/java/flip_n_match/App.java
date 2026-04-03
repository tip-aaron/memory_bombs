package flip_n_match;

import flip_n_match.audio.AudioManager;
import flip_n_match.game.settings.GameDifficulty;
import flip_n_match.game.settings.UserSettings;
import flip_n_match.lib.Scorer;
import flip_n_match.ui.themes.ThemeManager;

import javax.swing.*;
import java.util.Random;

public class App {
    private static MainFrame mainFrame;

    public static void close() {
        mainFrame.dispose();
        System.exit(0);
    }

    public static void main(final String[] args) {
        ThemeManager.manage();

        generateDummyLeaderboardData();

        SwingUtilities.invokeLater(() -> {
            mainFrame = new MainFrame();
            mainFrame.setVisible(true);

            if (UserSettings.getInstance().getAudio().musicEnabled()
                    .get()) {
                AudioManager.getInstance().playMusic(
                        UserSettings.getInstance().getAudio()
                                .selectedMusic().get()
                );
            }
        });
    }

    private static void generateDummyLeaderboardData() {
        if (!Scorer.getSortedScores().isEmpty()) {
            return;
        }

        Random rand = new Random();

        for (GameDifficulty difficulty : GameDifficulty.values()) {
            for (int i = 1; i <= 10; i++) {
                String dummyName = "Tester_" + difficulty.name() + "_" + i;
                long dummyTime = 30 + rand.nextInt(270);

                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Scorer.saveScore(dummyName, dummyTime, difficulty.name());
            }
        }

        System.out.println("Dummy leaderboard data successfully injected!");
    }
}