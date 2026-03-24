package flip_n_match.game.controls;

import lombok.Getter;

public enum GameAction {
    REVEAL_TILE("Reveal / Flip Tile"),
    FLAG_MINE("Flag Mine"),
    CHORD("Chord (Reveal Adjacent)"),
    PAUSE_MENU("Pause Game / Menu");

    @Getter
    private final String displayName;

    GameAction(String displayName) {
        this.displayName = displayName;
    }

    public String toDetailedString() {
        return this.displayName;
    }
}
