package flip_n_match.ui.pages.game;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.game.SpecialTile;
import flip_n_match.game.Tile;
import flip_n_match.game.TileStatus;
import flip_n_match.ui.icons.SVGIconUIColor;

import javax.swing.*;
import java.util.HashMap;

public class TileViewRenderer {
    private final HashMap<String, SVGIconUIColor> cachedIcons = new HashMap<>();

    public void render(JButton btn, Tile tile) {
        if (tile == null) {
            renderNullOrHidden(btn);
            return;
        }

        switch (tile.getStatus()) {
            case TileStatus.REVEALED -> renderRevealed(btn, tile);
            case TileStatus.HIDDEN -> renderNullOrHidden(btn);
            case TileStatus.FLAGGED -> renderFlagged(btn);
        }
    }

    private void renderNullOrHidden(JButton btn) {
        btn.setText("");
        btn.setIcon(null);
        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "hidden");
        btn.setEnabled(true);
    }

    private void renderFlagged(JButton btn) {
        btn.setText("");
        btn.setIcon(getIcon("flag.svg", "foreground.flagged"));
        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "flagged");
        btn.setEnabled(true);
    }

    private void renderRevealed(JButton btn, Tile tile) {
        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "revealed");

        btn.setRequestFocusEnabled(false);

        switch (tile) {
            case Tile.Explosive ignored -> {
                btn.setIcon(getIcon("bomb.svg", "color.error"));
                btn.setText("");
                btn.setEnabled(false);
            }
            case Tile.ClueProvider clue when !clue.isEmpty() -> {
                btn.setText(String.valueOf(clue.getAdjacentHazardCount()));
                btn.setIcon(null);
                btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "foreground.revealed");

                btn.setEnabled(true);
            }
            case Tile.Matchable matchable -> {
                btn.setText("");

                if (matchable.getIsMatched()) {
                    btn.setIcon(getUpdatedIcon(matchable.getMatchId(), "foreground.success", btn));
                    btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "matched");
                    btn.setEnabled(false);
                } else if (matchable.isSymbolRevealed()) {
                    btn.setIcon(getUpdatedIcon(matchable.getMatchId(), "foreground.revealed", btn));
                    btn.setEnabled(false);
                } else {
                    btn.setIcon(null);
                    btn.setEnabled(true);
                }
            }
            // non-existent right now.
            case SpecialTile ignored -> {
                btn.setText("");
                btn.setIcon(null);
                btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
                btn.setEnabled(false);
            }
            case Tile.ClueProvider ignored -> {
                btn.setText("");
                btn.setIcon(null);
                btn.setEnabled(false); // No need to chord an empty tile
            }
            default -> {
                btn.setText("");
                btn.setIcon(null);
                btn.setEnabled(false);
            }
        }
    }

    // --- Caching Helpers ---
    private SVGIconUIColor getIcon(String name, String colorKey) {
        cachedIcons.putIfAbsent(name, new SVGIconUIColor(name, 0.85f, colorKey));

        return cachedIcons.get(name);
    }

    private SVGIconUIColor getUpdatedIcon(String matchId, String newColor, JButton btn) {
        if (btn.getIcon() instanceof SVGIconUIColor icon) {
            icon.setColorKey(newColor);
            return icon;
        }

        return getIcon(matchId, newColor);
    }
}