package flip_n_match.ui.pages.game;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.config.UserSettings;
import flip_n_match.game.*;
import flip_n_match.ui.icons.SVGIconUIColor;
import lombok.Builder;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

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
    private final HashMap<String, SVGIconUIColor> cachedIcons;

    public GamePanel(GameState gameState) {
        this.gameState = gameState;
        this.cachedIcons = new HashMap<>();
    }

    public void buildGrid() {
        removeAll();

        Board board = gameState.getBoard();

        if (board == null) return;

        buttonTiles = new ButtonTile[board.getRows()][board.getCols()];

        setLayout(new MigLayout(String.format("insets 0 8, gap 8, wrap %d, al center top", board.getCols()), "", ""));

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
                    btn.setIcon(null);
                    btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "hidden");
                    btn.setEnabled(true);

                    continue;
                }

                switch (tile.getStatus()) {
                    case TileStatus.REVEALED -> {
                        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "revealed");

                        switch (tile) {
                            case Tile.Explosive ignored -> {
                                cachedIcons.putIfAbsent("bomb.svg", new SVGIconUIColor(
                                        "bomb.svg",
                                        1f,
                                        "color.error"
                                ));
                                SVGIconUIColor icon = cachedIcons.get("bomb.svg");

                                btn.setIcon(icon);
                                btn.setEnabled(false);
                            }
                            case Tile.ClueProvider clue when !clue.isEmpty() -> {
                                btn.setText(clue.isEmpty() ? "" : String.valueOf(clue.getAdjacentHazardCount()));
                                btn.setEnabled(false);
                            }
                            case Tile.Matchable matchable -> {
                                if (matchable.getIsMatched()) {
                                    if (btn.getIcon() != null && btn.getIcon() instanceof SVGIconUIColor icon) {
                                        icon.setColorKey("foreground.success");
                                    } else {
                                        cachedIcons.putIfAbsent(matchable.getMatchId(), new SVGIconUIColor(
                                                matchable.getMatchId(),
                                                1f,
                                                "foreground.success"
                                        ));
                                        SVGIconUIColor icon = cachedIcons.get(matchable.getMatchId());
                                        btn.setIcon(icon);
                                    }

                                    btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "matched");
                                    btn.setEnabled(false);
                                } else if (matchable.isSymbolRevealed()) {
                                    if (btn.getIcon() != null && btn.getIcon() instanceof SVGIconUIColor icon) {
                                        icon.setColorKey("foreground.revealed");
                                    } else {
                                        cachedIcons.putIfAbsent(matchable.getMatchId(), new SVGIconUIColor(
                                                matchable.getMatchId(),
                                                1f,
                                                "foreground.revealed"
                                        ));
                                        SVGIconUIColor icon = cachedIcons.get(matchable.getMatchId());
                                        btn.setIcon(icon);
                                    }
                                    btn.setEnabled(false);
                                } else {
                                    btn.setIcon(null);
                                    btn.setEnabled(true);
                                }
                            }
                            case SpecialTile ignored -> {
                                btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
                                btn.setEnabled(false);
                            }
                            case Tile.ClueProvider ignored -> {
                                btn.setText("");
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
