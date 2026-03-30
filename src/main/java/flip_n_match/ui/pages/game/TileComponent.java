package flip_n_match.ui.pages.game;

import flip_n_match.game.Coordinate;
import flip_n_match.game.GameState;
import flip_n_match.game.Tile;
import lombok.Getter;

import javax.swing.*;

public class TileComponent {
    @Getter
    private final JButton button;
    private final Coordinate coordinate;
    private final GameState gameState;
    private final TileViewRenderer renderer;

    public TileComponent(Coordinate coordinate, GameState gameState, TileViewRenderer renderer, Runnable onRefreshNeeded) {
        this.button = new JButton();
        this.coordinate = coordinate;
        this.gameState = gameState;
        this.renderer = renderer;

        this.button.setFocusable(true);

        TileInteractionController controller = new TileInteractionController(gameState, coordinate, onRefreshNeeded);

        this.button.addMouseListener(controller);
        this.button.addKeyListener(controller);
    }

    public void refresh() {
        Tile tile = gameState.getBoard().getTile(coordinate);

        renderer.render(button, tile);
    }
}
