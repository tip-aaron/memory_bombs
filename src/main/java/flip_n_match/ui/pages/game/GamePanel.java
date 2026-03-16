package flip_n_match.ui.pages.game;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.config.UserSettings;
import flip_n_match.game.*;
import lombok.Builder;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@Builder
@Getter
class ButtonTile {
    private JButton button;
    private ActionListener listener;
    private MouseListener mouseListener;
}

public class GamePanel extends JPanel {
    private final GameState gameState;
    private ButtonTile[][] buttonTiles;

    public GamePanel(GameState gameState) {
        this.gameState = gameState;
    }

    public void buildGrid() {
        removeAll(); // Crucial: clear old buttons if restarting the game

        Board board = gameState.getBoard();

        if (board == null) return;

        buttonTiles = new ButtonTile[board.getRows()][board.getCols()];

        setLayout(new MigLayout(String.format("insets 0 8, gap 8, wrap %d, al center top", board.getCols())));

        createButtonTiles(board.getRows(), board.getCols());
    }

    private void createButtonTiles(int rows, int cols) {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                Coordinate coordinate = Coordinate.builder().col(c).row(r).build();
                JButton btn = new JButton();
                ActionListener listener = ignored -> {
                    gameState.onTileClicked(coordinate);
                    refresh();
                };
                MouseListener mouseListener = new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e) && btn.isEnabled()) {
                            gameState.onTileRightClicked(coordinate);
                            refresh();
                        }
                    }
                };

                ButtonTile buttonTile = ButtonTile.builder().button(btn).listener(listener)
                        .mouseListener(mouseListener).build();

                btn.setFocusable(false);
                btn.addActionListener(listener);
                btn.addMouseListener(mouseListener);

                buttonTiles[r][c] = buttonTile;

                add(btn, "w 32!, h 32!");
            }
        }

        revalidate();
        repaint();
        refresh();
    }

    void refresh() {
        Board board = gameState.getBoard();

        if (board == null || buttonTiles == null) {
            return;
        }

        for (int r = 0; r < board.getRows(); ++r) {
            for (int c = 0; c < board.getCols(); ++c) {
                Tile tile = board.getTile(Coordinate.builder().col(c).row(r).build());
                JButton btn = buttonTiles[r][c].getButton();

                if (tile == null) {
                    btn.setText("");
                    btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "hidden");
                    btn.setEnabled(true);

                    continue;
                }

                switch (tile.getStatus()) {
                    case TileStatus.REVEALED -> {
                        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "revealed");

                        switch (tile) {
                            case Tile.Explosive ignored -> {
                                btn.setText("💣");
                                btn.setEnabled(false);
                            }
                            case Tile.ClueProvider clue when !clue.isEmpty() -> {
                                btn.setText(clue.isEmpty() ? "" : String.valueOf(clue.getAdjacentHazardCount()));
                                btn.setEnabled(false);
                            }
                            case Tile.Matchable matchable -> {
                                if (matchable.getIsMatched()) {
                                    btn.setText(matchable.getMatchId());
                                    btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "matched");
                                    btn.setEnabled(false);
                                } else if (matchable.isSymbolRevealed()) {
                                    btn.setText(matchable.getMatchId());
                                    btn.setEnabled(false);
                                } else {
                                    btn.setText("");
                                    btn.setEnabled(true);
                                }
                            }
                            case Tile.ClueProvider ignored -> {
                                btn.setText("");
                                btn.setEnabled(false);
                            }
                            case SpecialTile specialTile -> {
                                btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
                                btn.setEnabled(false);
                            }
                            default -> {}
                        }
                    }
                    case TileStatus.HIDDEN -> {
                        btn.setText("");
                        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "hidden");
                        btn.setEnabled(true);
                    }
                    case TileStatus.FLAGGED -> {
                        btn.setText("🚩");
                        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "flagged");
                        btn.setEnabled(true);
                    }
                }
            }
        }
    }

    void open() {
        UserSettings settings = UserSettings.getInstance();

        // LIFECYCLE FIX: Check if we are resuming or starting fresh
        if (gameState.getBoard() == null || gameState.isGameOver() || gameState.getBoard().getRows() == 0) {
            // 1. START FRESH
            gameState.initializeGame(
                    settings.getCurrentDifficulty().getRows(),
                    settings.getCurrentDifficulty().getCols(),
                    settings.getCurrentDifficulty().getMineCount()
            );
            buildGrid();
        }

        gameState.resumeGame();
    }

    void close() {
        gameState.pauseGame();
    }

    void destroy() {
        gameState.getBoard().reset();
        gameState.getStopwatch().stop();
        gameState.getBoard().reset();

        refresh();
    }
}
