package flip_n_match.game.settings;

import flip_n_match.game.controls.GameAction;
import flip_n_match.game.controls.InputBinding;
import lombok.Getter;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class ControlSettings {
    @Getter
    private final Map<GameAction, SettingsProperty<InputBinding>> bindings = new EnumMap<>(GameAction.class);

    // Add a list of callbacks
    private final List<Runnable> onSettingsChangedListeners = new ArrayList<>();

    public ControlSettings(Preferences prefs) {
        bindings.put(GameAction.REVEAL_TILE, new SettingsProperty<>(
                "keybinds_" + GameAction.REVEAL_TILE.name(),
                new InputBinding(InputBinding.Type.MOUSE, MouseEvent.BUTTON1),
                prefs
        ));

        bindings.put(GameAction.FLAG_MINE, new SettingsProperty<>(
                "keybinds_" + GameAction.FLAG_MINE.name(),
                new InputBinding(InputBinding.Type.MOUSE, MouseEvent.BUTTON3),
                prefs
        ));

        bindings.put(GameAction.CHORD, new SettingsProperty<>(
                "keybinds_" + GameAction.CHORD.name(),
                new InputBinding(InputBinding.Type.MOUSE, MouseEvent.BUTTON2),
                prefs
        ));

        bindings.put(GameAction.PAUSE_MENU, new SettingsProperty<>(
                "keybinds_" + GameAction.PAUSE_MENU.name(),
                new InputBinding(InputBinding.Type.KEYBOARD, KeyEvent.VK_ESCAPE),
                prefs
        ));
    }

    public SettingsProperty<InputBinding> getAction(GameAction action) {
        return bindings.get(action);
    }

    public void addChangeListener(Runnable listener) {
        onSettingsChangedListeners.add(listener);
    }

    public void removeChangeListener(Runnable listener) {
        onSettingsChangedListeners.remove(listener);
    }

    public void notifyListeners() {
        for (Runnable listener : onSettingsChangedListeners) {
            listener.run();
        }
    }
}
