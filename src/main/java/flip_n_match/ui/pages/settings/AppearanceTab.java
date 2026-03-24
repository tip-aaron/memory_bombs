package flip_n_match.ui.pages.settings;

import flip_n_match.config.GameTheme;
import flip_n_match.config.UserSettings;
import flip_n_match.ui.icons.SVGIconUIColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

@TabInfo(name = "Appearance")
public class AppearanceTab extends ASettingsTab {
    private final JComboBox<GameTheme> themeJComboBox;
    private final ItemListener themeItemListener;
    private GameTheme chosenTheme;

    public AppearanceTab() {
        super();

        addSectionHeader("Theme & Style", "");

        themeJComboBox = new JComboBox<>(GameTheme.values());
        themeItemListener = new ThemeItemListener();

        themeJComboBox.setRenderer(new ThemeJComboBoxRenderer());

        add(new JLabel("App Theme: "));
        add(themeJComboBox);

        revertChanges();
    }

    @Override
    public void open() {
        themeJComboBox.addItemListener(themeItemListener);
    }

    @Override
    public void close() {
        themeJComboBox.removeItemListener(themeItemListener);
    }

    @Override
    public boolean isDirty() {
        return chosenTheme != UserSettings.getInstance().getCurrentTheme();
    }

    @Override
    public void applyChanges() {
        if (isDirty()) {
            UserSettings.getInstance().setTheme(chosenTheme);
        }
    }

    @Override
    public void revertChanges() {
        chosenTheme = UserSettings.getInstance().getCurrentTheme();

        themeJComboBox.setSelectedItem(chosenTheme);
    }

    @Override
    public void loadDefaults() {
        chosenTheme = UserSettings.getInstance().getDEFAULT_THEME();

        themeJComboBox.setSelectedItem(UserSettings.getInstance().getDEFAULT_THEME());
    }

    static class ThemeJComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof GameTheme gameTheme) {
                setIcon(new SVGIconUIColor(gameTheme.getIconPath(), 1f, "foreground.background"));
                setText(gameTheme.toString());
            }

            return this;
        }
    }

    class ThemeItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                chosenTheme = (GameTheme) e.getItem();

                notifyDirty();
            }
        }
    }
}
