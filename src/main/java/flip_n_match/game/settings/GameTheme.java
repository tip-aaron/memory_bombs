package flip_n_match.game.settings;

import lombok.Getter;

public enum GameTheme {
    LIGHT("sun.svg"),
    DARK("moon.svg");

    @Getter
    private final String iconPath;

    GameTheme(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

