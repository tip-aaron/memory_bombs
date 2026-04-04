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
}