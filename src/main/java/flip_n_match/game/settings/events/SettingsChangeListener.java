package flip_n_match.game.settings.events;

@FunctionalInterface
public interface SettingsChangeListener<T> {
    void onValueChanged(T oldValue, T newValue);
}
