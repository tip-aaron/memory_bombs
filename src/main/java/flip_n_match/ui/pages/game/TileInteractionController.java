package flip_n_match.ui.pages.game;

import flip_n_match.audio.AudioManager;
import flip_n_match.game.*;
import flip_n_match.game.controls.GameAction;
import flip_n_match.game.controls.InputBinding;
import flip_n_match.game.settings.ControlSettings;
import flip_n_match.game.settings.UserSettings;
import flip_n_match.ui.pages.PageGameMenu;
import flip_n_match.ui.system.Navigator;
import lombok.Builder;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TileInteractionController extends MouseAdapter implements KeyListener {
    private final GameState gameState;
    private final Coordinate coordinate;
    private final Runnable refreshCallback;

    @Builder
    public TileInteractionController(GameState gameState, Coordinate coordinate, Runnable refreshCallback) {
        this.gameState = gameState;
        this.coordinate = coordinate;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Tile tile = gameState.getBoard().getTile(coordinate);

        if (tile == null || tile.getStatus() == TileStatus.HIDDEN || tile.getStatus() == TileStatus.FLAGGED) {
            AudioManager.getInstance().playSfx(AudioManager.Sfx.BUTTON_HOVER);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleInput(InputBinding.Type.MOUSE, e.getButton());
    }

    @Override
    public void mouseReleased(MouseEvent e) { /* Ignored */ }

    @Override
    public void keyPressed(KeyEvent e) {
        handleInput(InputBinding.Type.KEYBOARD, e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) { /* Ignored */ }

    @Override
    public void keyTyped(KeyEvent e) { /* Ignored */ }

    private void handleInput(InputBinding.Type inputType, int keyCode) {
        ControlSettings controls = UserSettings.getInstance().getControls();

        if (isBindingMatch(controls.getAction(GameAction.REVEAL_TILE).get(), inputType, keyCode)) {
            executeReveal();
        } else if (isBindingMatch(controls.getAction(GameAction.FLAG_MINE).get(), inputType, keyCode)) {
            executeFlag();
        } else if (isBindingMatch(controls.getAction(GameAction.CHORD).get(), inputType, keyCode)) {
            executeChord();
        } else if (isBindingMatch(controls.getAction(GameAction.PAUSE_MENU).get(), inputType, keyCode)) {
            executePause();
        }
    }

    private boolean isBindingMatch(InputBinding binding, InputBinding.Type type, int code) {
        return binding != null && binding.type() == type && binding.keyCode() == code;
    }

    // ==========================================
    // ACTION EXECUTIONS & SOUND TRIGGERS
    // ==========================================

    private void executeReveal() {
        Tile tile = gameState.getBoard().getTile(coordinate);

        if (tile == null || tile.getStatus() == TileStatus.HIDDEN || tile.getStatus() == TileStatus.REVEALED) {
            gameState.onTileReveal(coordinate);

            Tile updatedTile = gameState.getBoard().getTile(coordinate);

            if (updatedTile instanceof MineTile) {
                AudioManager.getInstance().playSfx(AudioManager.Sfx.BOMB_REVEAL);
            } else if (updatedTile instanceof Tile.Matchable matchable && matchable.getIsMatched()) {
                AudioManager.getInstance().playSfx(AudioManager.Sfx.TILE_MATCHED);
            }
            // Standard reveal
            else {
                AudioManager.getInstance().playSfx(AudioManager.Sfx.TILE_REVEAL);
            }

            refreshCallback.run();
        }
    }

    private void executeFlag() {
        Tile tile = gameState.getBoard().getTile(coordinate);

        if (tile != null && tile.getStatus() != TileStatus.FLAGGED &&
                gameState.getBoard().countFlags() == UserSettings.getInstance().getGameplay().difficulty().get().getMineCount()) {

            AudioManager.getInstance().playSfx(AudioManager.Sfx.BUTTON_CLICK); // General error click
            JOptionPane.showMessageDialog(null, "You have flagged all possible mine locations. Please unflag some to flag again.");
            return;
        }

        if (tile == null || tile.getStatus() != TileStatus.REVEALED) {
            // Check state BEFORE action to know if we are flagging or unflagging
            boolean wasFlagged = (tile != null && tile.getStatus() == TileStatus.FLAGGED);

            gameState.onTileFlag(coordinate);

            // Play the appropriate sound
            AudioManager.getInstance().playSfx(wasFlagged ? AudioManager.Sfx.UNFLAG : AudioManager.Sfx.FLAG);

            refreshCallback.run();
        }
    }

    private void executeChord() {
        Tile tile = gameState.getBoard().getTile(coordinate);

        if (tile == null || tile.getStatus() == TileStatus.REVEALED) {
            performChordLogic(tile);
            refreshCallback.run();
        }
    }

    private void executePause() {
        if (!gameState.isGameOver()) {
            Navigator.navigate(PageGameMenu.class);
        }
    }

    private void performChordLogic(Tile tile) {
        if (!(tile instanceof Tile.ClueProvider clue)) return;

        int requiredFlags = clue.getAdjacentHazardCount();
        if (requiredFlags == 0) return;

        Board board = gameState.getBoard();
        int r = coordinate.getRow();
        int c = coordinate.getCol();

        int knownHazardCount = 0;
        int hiddenCount = 0;
        List<Coordinate> hiddenNeighbors = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Coordinate neighbor = Coordinate.builder().row(r + i).col(c + j).build();
                Tile nTile = board.getTile(neighbor);

                if (nTile != null) {
                    if (nTile.getStatus() == TileStatus.FLAGGED) {
                        knownHazardCount++;
                    } else if (nTile instanceof MineTile && nTile.getStatus() != TileStatus.HIDDEN) {
                        knownHazardCount++;
                    } else if (nTile.getStatus() == TileStatus.HIDDEN) {
                        hiddenCount++;
                        hiddenNeighbors.add(neighbor);
                    }
                }
            }
        }

        boolean cascadeTriggered = false;

        // Scenario A
        if (knownHazardCount == requiredFlags) {
            for (Coordinate hidden : hiddenNeighbors) {
                gameState.onTileReveal(hidden);
                cascadeTriggered = true;
            }
        }
        // Scenario B
        else if (UserSettings.getInstance().getGameplay().autoFlag().get() && hiddenCount > 0 && hiddenCount == (requiredFlags - knownHazardCount)) {
            for (Coordinate hidden : hiddenNeighbors) {
                gameState.onTileFlag(hidden);
                cascadeTriggered = true;
            }
        }

        // Play the chord sound ONLY if tiles were actually modified
        if (cascadeTriggered) {
            AudioManager.getInstance().playSfx(AudioManager.Sfx.CHORD_CASCADE);
            AudioManager.getInstance().playSfx(AudioManager.Sfx.BOMB_REVEAL);
            AudioManager.getInstance().playSfx(AudioManager.Sfx.GAME_OVER);
        }
    }
}