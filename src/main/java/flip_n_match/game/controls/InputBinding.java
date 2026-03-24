package flip_n_match.game.controls;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public record InputBinding(Type type, int keyCode) {
    public enum Type {MOUSE, KEYBOARD}

    public String serialize() {
        return type.name() + ":" + keyCode;
    }

    public static InputBinding deserialize(String data) {
        try {
            String[] parts = data.split(":");

            return new InputBinding(Type.valueOf(parts[0]), Integer.parseInt(parts[1]));
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public @NotNull String toString() {
        if (type == Type.MOUSE) {
            return switch (keyCode) {
                case MouseEvent.BUTTON1 -> "Left Click";
                case MouseEvent.BUTTON2 -> "Middle Click";
                case MouseEvent.BUTTON3 -> "Right Click";
                default -> "Mouse " + keyCode;
            };
        } else {
            return KeyEvent.getKeyText(keyCode);
        }
    }
}
