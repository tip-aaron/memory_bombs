package flip_n_match.ui.pages.game;

import com.formdev.flatlaf.FlatClientProperties;
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
        btn.setIcon(getIcon("flag.svg", "foreground.error")); // Use color.error for flags
        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "flagged");
        btn.setEnabled(true);
    }

    private void renderRevealed(JButton btn, Tile tile) {
        // Base class applied first
        btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "revealed");
        btn.setRequestFocusEnabled(false);

        switch (tile) {
            case Tile.Explosive ignored -> {
                btn.setIcon(getIcon("bomb.svg", "color.error"));
                btn.setText("");
                btn.setEnabled(false);
            }
            case Tile.ClueProvider clue when !clue.isEmpty() -> {
                int mineCount = clue.getAdjacentHazardCount();
                btn.setText(String.valueOf(mineCount));
                btn.setIcon(null);

                // Combine the base "revealed" style with the specific "numX" color style
                btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "num" + mineCount);
                btn.setEnabled(true);
            }
            case Tile.Matchable matchable -> {
                btn.setText("");

                if (matchable.getIsMatched()) {
                    btn.setIcon(getIcon(matchable.getMatchId(), "color.success"));
                    // Combine styles so it keeps the button shape/background logic
                    btn.putClientProperty(FlatClientProperties.STYLE_CLASS, "revealed matched");
                    btn.setEnabled(false);
                } else if (matchable.isSymbolRevealed()) {
                    // Just revealed, not matched yet
                    btn.setIcon(getIcon(matchable.getMatchId(), "foreground.revealed"));
                    btn.setEnabled(false);
                } else {
                    btn.setIcon(null);
                    btn.setEnabled(true);
                }
            }
            default -> {
                btn.setText("");
                btn.setIcon(null);
                btn.setEnabled(false);
            }
        }
    }

    // --- Fixed Caching Helpers ---

    private SVGIconUIColor getIcon(String name, String colorKey) {
        // Use a composite key so red bombs and green bombs don't overwrite each other!
        String cacheKey = name + "_" + colorKey;

        cachedIcons.putIfAbsent(cacheKey, new SVGIconUIColor(name, 0.85f, colorKey));
        return cachedIcons.get(cacheKey);
    }
}