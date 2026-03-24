package flip_n_match.ui.pages.game;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.game.*;
import flip_n_match.game.controls.GameAction;
import flip_n_match.game.controls.InputBinding;
import flip_n_match.game.settings.ControlSettings;
import flip_n_match.game.settings.UserSettings;
import flip_n_match.ui.icons.IconCache;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Getter
public class ButtonTile {
    private final JButton button;
    private final Coordinate coordinate;
    private  MouseAdapter mouseAdapter;

    public ButtonTile(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.button = new JButton();

        this.button.setFocusable(true);
    }

    public void attachInputs(GameState gameState, Runnable trigger) {
        ControlSettings controls = UserSettings.getInstance().getControls();
        InputBinding revealBind = controls.getAction(GameAction.REVEAL_TILE).get();
        InputBinding flagBind = controls.getAction(GameAction.FLAG_MINE).get();

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isInteractable()) {
                    return;
                }

                if (revealBind.type() == InputBinding.Type.MOUSE && e.getButton() == revealBind.keyCode()) {
                    gameState.onTileReveal(coordinate);
                } else if (flagBind.type() == InputBinding.Type.MOUSE && e.getButton() == flagBind.keyCode()) {
                    gameState.onTileFlag(coordinate);
                }

                trigger.run();
            }
        };

        button.addMouseListener(mouseAdapter);

        InputMap inputMap = button.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = button.getActionMap();

        if (revealBind.type() == InputBinding.Type.KEYBOARD) {
            inputMap.put(KeyStroke.getKeyStroke(revealBind.keyCode(), 0), "revealAction");

            actionMap.put("revealAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isInteractable()) {
                        return;
                    }

                    gameState.onTileReveal(coordinate);
                    trigger.run();
                }
            });
        }

        if (flagBind.type() == InputBinding.Type.KEYBOARD) {
            inputMap.put(KeyStroke.getKeyStroke(flagBind.keyCode(), 0), "flagAction");
            actionMap.put("flagAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isInteractable()) {
                        return;
                    }

                    gameState.onTileFlag(coordinate);
                    trigger.run();
                }
            });
        }
    }

    /**
     * Safely detaches old inputs so we can attach new ones.
     */
    public void cleanupInputs() {
        if (mouseAdapter != null) {
            button.removeMouseListener(mouseAdapter);
            mouseAdapter = null;
        }
        button.getInputMap(JComponent.WHEN_FOCUSED).clear();
        button.getActionMap().clear();
    }

    /**
     * Used when the tile is completely destroyed.
     */
    public void cleanup() {
        cleanupInputs();

        button.setIcon(null);
        button.setText("");
        button.putClientProperty(FlatClientProperties.STYLE_CLASS, null);
    }

    /**
     * Refreshes the active listeners dynamically.
     */
    public void rebindInputs(GameState gameState, Runnable trigger) {
        cleanupInputs();
        attachInputs(gameState, trigger);
    }

    public void render(Tile tile) {
        if (tile == null) {
            resetVisuals();

            return;
        }

        switch (tile.getStatus()) {
            case TileStatus.REVEALED -> renderRevealed(tile);
            case TileStatus.HIDDEN -> renderHidden();
            case TileStatus.FLAGGED -> renderFlagged();
        }
    }
    // THERES BUG> FIX IT

    private void renderRevealed(Tile tile) {
        button.putClientProperty(FlatClientProperties.STYLE_CLASS, "revealed");

        switch (tile) {
            case Tile.Explosive ignored -> {
                button.setIcon(IconCache.get("bomb.svg", "color.error"));
                button.setEnabled(false);
            }
            case Tile.ClueProvider clue when !clue.isEmpty() -> {
                button.setText(String.valueOf(clue.getAdjacentHazardCount()));
                button.setEnabled(false);
            }
            case Tile.Matchable matchable -> {
                String matchId = matchable.getMatchId();
                if (matchable.getIsMatched()) {
                    button.setIcon(IconCache.get(matchId, "foreground.success"));
                    button.putClientProperty(FlatClientProperties.STYLE_CLASS, "matched");
                    button.setEnabled(false);
                } else if (matchable.isSymbolRevealed()) {
                    button.setIcon(IconCache.get(matchId, "foreground.revealed"));
                    button.setEnabled(false);
                } else {
                    button.setIcon(null);
                    button.setEnabled(true);
                }
            }
            case SpecialTile ignored -> {
                button.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
                button.setEnabled(false);
            }
            case Tile.ClueProvider ignored -> {
                button.setText("");
                button.setEnabled(false);
            }
            default -> {}
        }
    }

    private void renderHidden() {
        button.setText("");
        button.putClientProperty(FlatClientProperties.STYLE_CLASS, "hidden");
        button.setEnabled(true);
    }

    private void renderFlagged() {
        button.setText("🚩");
        button.putClientProperty(FlatClientProperties.STYLE_CLASS, "flagged");
        button.setEnabled(true);
    }

    private void resetVisuals() {
        button.setText("");
        button.setIcon(null);
        button.putClientProperty(FlatClientProperties.STYLE_CLASS, "hidden");
        button.setEnabled(true);
    }

    private boolean isInteractable() {
        return !button.isEnabled() && !"hidden".equals(button.getClientProperty(FlatClientProperties.STYLE_CLASS));
    }
}
