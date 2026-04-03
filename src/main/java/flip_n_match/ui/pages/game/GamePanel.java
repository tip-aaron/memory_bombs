package flip_n_match.ui.pages.game;

import flip_n_match.game.Board;
import flip_n_match.game.Coordinate;
import flip_n_match.game.GameState;
import flip_n_match.game.settings.GameDifficulty;
import flip_n_match.game.settings.UserSettings;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GamePanel extends JPanel {
    private final GameState gameState;
    private TileComponent[][] tileComponents;
    private final TileViewRenderer tileRenderer;

    public GamePanel(GameState gameState) {
        this.gameState = gameState;
        this.tileRenderer = new TileViewRenderer();
    }

    public void buildGrid() {
        removeAll();
        Board board = gameState.getBoard();

        if (board == null) return;

        int rows = board.getRows();
        int cols = board.getCols();

        tileComponents = new TileComponent[rows][cols];

        setLayout(new MigLayout("fill, insets 0", "[center, grow]", "[top, grow]"));

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                Coordinate coordinate = Coordinate.builder().col(c).row(r).build();
                TileComponent tileComp = new TileComponent(coordinate, gameState, tileRenderer, this::refresh);

                tileComponents[r][c] = tileComp;

                JButton btn = tileComp.getButton();

                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setMinimumSize(new Dimension(0, 0));

                gridPanel.add(btn);
            }
        }

        int MAX_BUTTON_SIZE = 80;

        wrapperPanel.add(gridPanel);

        wrapperPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int margin = 32; // 32px breathing room around the edges
                int size = Math.min(wrapperPanel.getWidth(), wrapperPanel.getHeight()) - margin;

                int maxGridSize = Math.max(rows, cols) * MAX_BUTTON_SIZE;

                size = Math.min(Math.max(size, 10), maxGridSize);

                gridPanel.setPreferredSize(new Dimension(size, size));
                wrapperPanel.revalidate();
            }
        });

        add(wrapperPanel, "grow, push");

        revalidate();
        repaint();
        refresh();
    }

    public void refresh() {
        Board board = gameState.getBoard();

        if (board == null || tileComponents == null) return;

        for (int r = 0; r < board.getRows(); ++r) {
            for (int c = 0; c < board.getCols(); ++c) {
                if (tileComponents[r][c] != null) {
                    tileComponents[r][c].refresh();
                }
            }
        }
    }

    public void open() {
        UserSettings settings = UserSettings.getInstance();

        if (gameState.getBoard() == null || gameState.isGameOver() || gameState.getBoard().getRows() == 0) {
            GameDifficulty gameDifficulty = settings.getGameplay().difficulty().get();

            gameState.initializeGame(
                    gameDifficulty.getRows(),
                    gameDifficulty.getCols(),
                    gameDifficulty.getMineCount()
            );
            buildGrid();
        }

        gameState.resumeGame();
    }

    public void close() {
        gameState.pauseGame();
    }

    public void destroy() {
        for (int r = 0; r < gameState.getBoard().getRows(); ++r) {
            for (int c = 0; c < gameState.getBoard().getCols(); ++c) {
                if (tileComponents[r][c] != null) {
                    tileComponents[r][c] = null;
                }
            }
        }

        gameState.reset();
        removeAll();
    }
}