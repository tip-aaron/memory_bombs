package flip_n_match.ui.pages.settings;

import flip_n_match.config.GameDifficulty;
import flip_n_match.config.UserSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

@TabInfo(name = "Gameplay")
public class GameplayTab extends ASettingsTab {
    private final JComboBox<GameDifficulty> difficultyJComboBox;
    private final ItemListener difficultyItemListener;
    private GameDifficulty chosenDifficulty;

    public GameplayTab() {
        super();

        addSectionHeader("General Rules","If you are in-game, then some changes will only take effect after a new game.");

        difficultyJComboBox = new JComboBox<>(GameDifficulty.values());
        difficultyItemListener = new DifficultyItemListener();

        difficultyJComboBox.setRenderer(new DifficultyJComboBoxRenderer());

        add(new JLabel("Difficulty: "));
        add(difficultyJComboBox);

        revertChanges();
    }

    @Override
    public void open() {
        difficultyJComboBox.addItemListener(difficultyItemListener);
    }

    @Override
    public void close() {
        difficultyJComboBox.removeItemListener(difficultyItemListener);
    }

    @Override
    public boolean isDirty() {
        return chosenDifficulty != UserSettings.getInstance().getCurrentDifficulty();
    }

    @Override
    public void applyChanges() {
        if (isDirty()) {
            UserSettings.getInstance().setDifficulty(chosenDifficulty);
        }
    }

    @Override
    public void revertChanges() {
        chosenDifficulty = UserSettings.getInstance().getCurrentDifficulty();

        difficultyJComboBox.setSelectedItem(chosenDifficulty);
    }

    @Override
    public void loadDefaults() {
        chosenDifficulty = UserSettings.getInstance().getDEFAULT_DIFFICULTY();

        difficultyJComboBox.setSelectedItem(chosenDifficulty);
    }

    static class DifficultyJComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof GameDifficulty gameDifficulty) {
                setText(gameDifficulty.toDetailedString());
            }

            return this;
        }
    }

    class DifficultyItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                chosenDifficulty = (GameDifficulty) e.getItem();

                notifyDirty();
            }
        }
    }
}
