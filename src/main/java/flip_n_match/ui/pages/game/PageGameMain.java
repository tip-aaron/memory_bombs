package flip_n_match.ui.pages.game;

import flip_n_match.game.GameState;
import flip_n_match.game.events.GameEventMessenger;
import flip_n_match.game.settings.UserSettings;
import flip_n_match.lib.Scorer;
import flip_n_match.ui.pages.PageStartMenu;
import flip_n_match.ui.pages.leaderboard.PageLeaderboard;
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
        setLayout(new MigLayout("gapy 48px, al center top", "[grow, fill]", "[][grow, fill, top]"));

        header = new Header();

        gameState = new GameState(
                str -> SwingUtilities.invokeLater(() -> {
                    if (header != null) header.setStopwatchText(str);
                }),
                () -> SwingUtilities.invokeLater(this::refreshUI)
        );

        gamePanel = new GamePanel(gameState);

        add(header, "grow, wrap");
        add(gamePanel, "grow, push");
    }

    private void refreshUI() {
        if (gamePanel != null) {
            gamePanel.refresh();

            if (header != null && gameState.getBoard() != null) {
                int totalMines = UserSettings.getInstance().getGameplay().difficulty().get().getMineCount();
                int currentFlags = gameState.getBoard().countFlags();

                header.updateMineStats(currentFlags, totalMines);
            }
        }
    }

    @Override
    public void open() {
        header.open();
        gamePanel.open();

        refreshUI();

        GameEventMessenger.getInstance().addGameOverListener(isWin -> SwingUtilities.invokeLater(() -> {
            refreshUI();

            if (isWin) {
                long finalTimeRaw = gameState.getStopwatch().getElapsedSeconds();
                String difficulty = UserSettings.getInstance().getGameplay().difficulty().get().toString();

                String playerName = JOptionPane.showInputDialog(
                        this,
                        "You Won on " + difficulty + " difficulty! Amazing job.\nEnter your name to save your score (Leave blank for Anonymous, or Cancel to skip):",
                        "Victory!",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (playerName != null) {
                    if (playerName.trim().isEmpty()) playerName = "Anonymous";
                    Scorer.saveScore(playerName, finalTimeRaw, difficulty);
                    String rankMsg = getRankMsg(playerName, finalTimeRaw, difficulty);
                    JOptionPane.showMessageDialog(this, "Score saved for " + playerName + "!" + rankMsg, "Saved", JOptionPane.INFORMATION_MESSAGE);
                }

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

    private static @NotNull String getRankMsg(String playerName, long finalTimeRaw, String difficulty) {
        java.util.List<Scorer.ScoreEntry> scores = Scorer.getSortedScores(difficulty);
        int rank = -1;
        for (int i = 0; i < scores.size(); i++) {
            Scorer.ScoreEntry entry = scores.get(i);
            if (entry.name().equals(playerName) && entry.timeValue() == finalTimeRaw) {
                rank = i + 1;
                break;
            }
        }
        return (rank > 0) ? "\nYou are currently in place #" + rank + " for " + difficulty + " difficulty!" : "";
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