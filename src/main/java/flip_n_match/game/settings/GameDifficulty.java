package flip_n_match.game.settings;

import lombok.Getter;

@Getter
public enum GameDifficulty {
    EASY(8, 8, 10),
    MEDIUM(10, 10, 15),
    HARD(12, 12, 25);

    private final int rows;
    private final int cols;
    private final int mineCount;

    GameDifficulty(final int rows, final int cols, final int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
    }

    public String toDetailedString() {
        return String.format("%s (%dx%d), %d mines", this, rows, cols, mineCount);
    }
}
