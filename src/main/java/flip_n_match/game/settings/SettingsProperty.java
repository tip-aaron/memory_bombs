package flip_n_match.game.settings;

import flip_n_match.game.controls.InputBinding;
import flip_n_match.game.settings.events.SettingsChangeListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

public class SettingsProperty<T> {
    private final String key;
    private final Preferences prefs;

    private T value;
    @Getter
    private final T defaultValue;
    private final List<SettingsChangeListener<T>> listeners = new ArrayList<>();

    public SettingsProperty(String key, T defaultValue, Preferences prefs) {
        this.key = key;
        this.prefs = prefs;
        this.value = loadFromPrefs(defaultValue);
        this.defaultValue = defaultValue;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        if (Objects.equals(this.value, newValue)) {
            return;
        }

        T oldValue = this.value;
        this.value = newValue;

        saveToPrefs(newValue);

        for (SettingsChangeListener<T> listener : listeners) {
            listener.onValueChanged(oldValue, newValue);
        }
    }

    public void addListener(SettingsChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(SettingsChangeListener<T> listener) { listeners.remove(listener); }

    @SuppressWarnings({"unchecked"})
    private T loadFromPrefs(T defaultValue) {
        return (T) switch (defaultValue) {
            case Integer i -> prefs.getInt(key, i);
            case Boolean b -> prefs.getBoolean(key, b);
            case String s -> prefs.get(key, s);
            case Enum<?> e -> {
                String enumName = prefs.get(key, e.name());

                try {
                    yield Enum.valueOf(e.getDeclaringClass(), enumName);
                } catch (IllegalArgumentException ex) {
                    yield e;
                }
            }
            case InputBinding ib -> {
                String savedBind = prefs.get(key, ib.serialize());
                InputBinding parsedBinding = InputBinding.deserialize(savedBind);

                yield (parsedBinding != null) ? parsedBinding : ib;
            }
            default -> throw new UnsupportedOperationException(
                    "Type not supported by SettingsProperty: " + defaultValue.getClass().getSimpleName()
            );
        };
    }

    private void saveToPrefs(T val) {
        switch (val) {
            case Integer i -> prefs.putInt(key, i);
            case Boolean b -> prefs.putBoolean(key, b);
            case String s -> prefs.put(key, s);
            case Enum<?> e -> prefs.put(key, e.name());
            case InputBinding ib -> prefs.put(key, ib.serialize());
            default -> throw new UnsupportedOperationException(
                    "Type not supported by SettingsProperty: " + val.getClass().getSimpleName()
            );
        }
    }
}
