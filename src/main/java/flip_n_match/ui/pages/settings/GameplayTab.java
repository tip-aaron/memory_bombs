package flip_n_match.ui.pages.settings;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.game.settings.GameDifficulty;
import flip_n_match.game.settings.UserSettings;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

@TabInfo(name = "Gameplay")
public class GameplayTab extends ASettingsTab {
    private final JComboBox<GameDifficulty> difficultyJComboBox;
    private final ItemListener difficultyItemListener;
    private final JCheckBox autoFlagCheckBox;
    private final ItemListener autoFlagItemListener;
    private GameDifficulty chosenDifficulty;
    private boolean chosenAutoFlag;

    public GameplayTab() {
        super();

        addSectionHeader("General Rules", "If you are in-game, then some changes will only take effect after a new game.");

        // --- Difficulty Setting ---
        difficultyJComboBox = new JComboBox<>(GameDifficulty.values());
        difficultyItemListener = new DifficultyItemListener();
        difficultyJComboBox.setRenderer(new DifficultyJComboBoxRenderer());

        add(new JLabel("Difficulty: "));
        add(difficultyJComboBox);

        JPanel container = new JPanel(new MigLayout("insets 0, al left top"));
        autoFlagCheckBox = new JCheckBox("Enabled", UserSettings.getInstance().getGameplay().autoFlag().get());
        autoFlagItemListener = new AutoFlagItemListener();
        JLabel autoFlagDesc = new JLabel("<html>Automatically flag mines around a numbered tile when possible.</html>");

        autoFlagDesc.putClientProperty(FlatClientProperties.STYLE_CLASS, "mini muted");
        autoFlagDesc.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        add(container);
        add(autoFlagCheckBox);

        container.add(new JLabel("Auto Flag: "), "wrap, gapbottom 4px");
        container.add(autoFlagDesc);

        revertChanges();
    }

    @Override
    public void open() {
        difficultyJComboBox.addItemListener(difficultyItemListener);
        autoFlagCheckBox.addItemListener(autoFlagItemListener);
    }

    @Override
    public void close() {
        difficultyJComboBox.removeItemListener(difficultyItemListener);
        autoFlagCheckBox.removeItemListener(autoFlagItemListener);
    }

    @Override
    public boolean isDirty() {
        return chosenDifficulty != UserSettings.getInstance().getGameplay().difficulty().get() ||
                chosenAutoFlag != UserSettings.getInstance().getGameplay().autoFlag().get();
    }

    @Override
    public void applyChanges() {
        if (isDirty()) {
            UserSettings.getInstance().getGameplay().difficulty().set(chosenDifficulty);
            UserSettings.getInstance().getGameplay().autoFlag().set(chosenAutoFlag);
        }
    }

    @Override
    public void revertChanges() {
        chosenDifficulty = UserSettings.getInstance().getGameplay().difficulty().get();
        chosenAutoFlag = UserSettings.getInstance().getGameplay().autoFlag().get();

        difficultyJComboBox.setSelectedItem(chosenDifficulty);
        autoFlagCheckBox.setSelected(chosenAutoFlag);
    }

    @Override
    public void loadDefaults() {
        chosenDifficulty = UserSettings.getInstance().getGameplay().difficulty().getDefaultValue();
        chosenAutoFlag = UserSettings.getInstance().getGameplay().autoFlag().getDefaultValue();

        difficultyJComboBox.setSelectedItem(chosenDifficulty);
        autoFlagCheckBox.setSelected(chosenAutoFlag);
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

    // --- Listeners ---

    class DifficultyItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                chosenDifficulty = (GameDifficulty) e.getItem();
                notifyDirty();
            }
        }
    }

    class AutoFlagItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            // JCheckBox uses SELECTED/DESELECTED
            chosenAutoFlag = autoFlagCheckBox.isSelected();
            notifyDirty();
        }
    }
}