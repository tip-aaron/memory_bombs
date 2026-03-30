package flip_n_match.ui.pages.game;

import flip_n_match.game.Board;
import flip_n_match.game.Coordinate;
import flip_n_match.game.GameState;
import flip_n_match.game.TileStatus;
import flip_n_match.game.settings.GameDifficulty;
import flip_n_match.game.settings.UserSettings;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

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
        setLayout(new MigLayout(String.format("insets 0 8, gap 8, wrap %d, al center top", cols), "", ""));

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                Coordinate coordinate = Coordinate.builder().col(c).row(r).build();
                TileComponent tileComp = new TileComponent(coordinate, gameState, tileRenderer, this::refresh);

                tileComponents[r][c] = tileComp;
                add(tileComp.getButton(), "w 32!, h 32!");
            }
        }

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
        if (gameState.getBoard() != null) {
            gameState.getBoard().reset();
        }

        gameState.getStopwatch().stop();

        refresh();
    }
}