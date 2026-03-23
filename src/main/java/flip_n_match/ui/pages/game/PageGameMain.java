package flip_n_match.ui.pages.game;

import flip_n_match.game.GameState;
import flip_n_match.game.events.GameEventMessenger;
import flip_n_match.ui.pages.PageStartMenu;
import flip_n_match.ui.system.Navigator;
import flip_n_match.ui.system.Page;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class PageGameMain extends Page {
    GameState gameState;
    Header header;
    GamePanel gamePanel;

    public PageGameMain() {
        setLayout(new MigLayout("gapy 64px, al center top", "[grow, fill]"));

        gameState = new GameState(
                str -> header.setStopwatchText(str),
                () -> gamePanel.refresh());
        header = new Header();
        gamePanel = new GamePanel(gameState);

        add(header, "grow, wrap");
        add(gamePanel);

        GameEventMessenger.getInstance().addGameOverListener(isWin -> SwingUtilities.invokeLater(() -> {
            gamePanel.refresh();

            String msg = isWin ? "You Won! Amazing job." : "Game Over! You hit a mine.";
            JOptionPane.showMessageDialog(this, msg, "Game Finished", JOptionPane.INFORMATION_MESSAGE);

            gameState.clearAndStop();
            // TODO: Proper clear
            gameState.initializeGame(0, 0, 0); // Hacky clear, or add a nullify method to GameState

            Navigator.navigate(PageStartMenu.class);
        }));
    }

    @Override
    public void open() {
        header.open();
        gamePanel.open();

        GameEventMessenger.getInstance().addGameOverListener(isWin -> SwingUtilities.invokeLater(() -> {
            gamePanel.refresh();

            String msg = isWin ? "You Won! Amazing job." : "Game Over! You hit a mine.";
            JOptionPane.showMessageDialog(this, msg, "Game Finished", JOptionPane.INFORMATION_MESSAGE);

            gameState.clearAndStop();
            // TODO: Proper clear
            gameState.initializeGame(0, 0, 0); // Hacky clear, or add a nullify method to GameState

            Navigator.navigate(PageStartMenu.class);
        }));
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
