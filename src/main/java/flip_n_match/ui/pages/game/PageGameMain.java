package flip_n_match.ui.pages.game;

import flip_n_match.game.GameState;
import flip_n_match.game.events.GameEventMessenger;
import flip_n_match.lib.Scorer;
import flip_n_match.ui.pages.PageLeaderboard;
import flip_n_match.ui.pages.PageStartMenu;
import flip_n_match.ui.system.Navigator;
import flip_n_match.ui.system.Page;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PageGameMain extends Page {
    final GameState gameState;
    Header header;
    GamePanel gamePanel;

    public PageGameMain() {
        setLayout(new MigLayout("gapy 64px, al center top", "[grow, fill]"));

        gameState = new GameState(
                str -> SwingUtilities.invokeLater(() -> header.setStopwatchText(str)),
                () -> SwingUtilities.invokeLater(() -> gamePanel.refresh()));
        header = new Header();
        gamePanel = new GamePanel(gameState);

        add(header, "grow, wrap");
        add(gamePanel);
    }

    @Override
    public void open() {
        header.open();
        gamePanel.open();

        GameEventMessenger.getInstance().addGameOverListener(isWin -> SwingUtilities.invokeLater(() -> {
            gamePanel.refresh();

            if (isWin) {
                long finalTimeRaw = gameState.getStopwatch().getElapsedSeconds();

                String playerName = JOptionPane.showInputDialog(
                        this,
                        "You Won! Amazing job.\nEnter your name to save your score:",
                        "Victory!",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (playerName == null || playerName.trim().isEmpty()) {
                    java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                    playerName = "Player_" + java.time.LocalDateTime.now().format(dtf);
                }

                Scorer.saveScore(playerName, finalTimeRaw);

                String rankMsg = getRankMsg(playerName, finalTimeRaw);
                JOptionPane.showMessageDialog(this, "Score saved for " + playerName + "!" + rankMsg, "Saved", JOptionPane.INFORMATION_MESSAGE);

                gameState.clearAndStop();
                gameState.initializeGame(0, 0, 0);

                Navigator.navigate(PageLeaderboard.class);
            } else {
                JOptionPane.showMessageDialog(this, "Game Over! You hit a mine.", "Game Finished", JOptionPane.INFORMATION_MESSAGE);

                gameState.clearAndStop();
                gameState.initializeGame(0, 0, 0);

                Navigator.navigate(PageStartMenu.class);
            }
        }));
    }

    private static @NotNull String getRankMsg(String playerName, long finalTimeRaw) {
        java.util.List<Scorer.ScoreEntry> scores = Scorer.getSortedScores();
        int rank = -1;

        for (int i = 0; i < scores.size(); i++) {
            Scorer.ScoreEntry entry = scores.get(i);

            // Match name and time to find their exact entry
            if (entry.name().equals(playerName) && entry.timeValue() == finalTimeRaw) {
                rank = i + 1;
                break;
            }
        }

        return (rank > 0) ? "\nYou are currently in place #" + rank + "!" : "";
    }

    @Override
    public void close() {
        header.close();
        gamePanel.close();

        GameEventMessenger.getInstance().removeAllListeners();
    }

    @Override
    public void destroy() {
        header.destroy();
        gamePanel.destroy();
        GameEventMessenger.getInstance().removeAllListeners();
    }
}
