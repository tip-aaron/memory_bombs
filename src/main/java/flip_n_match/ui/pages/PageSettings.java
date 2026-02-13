package flip_n_match.ui.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.FlatClientProperties;

import flip_n_match.config.GameDifficulty;
import flip_n_match.config.GameTheme;
import flip_n_match.config.UserSettings;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.system.Navigator;
import flip_n_match.ui.system.Page;
import net.miginfocom.swing.MigLayout;

public class PageSettings extends Page {
    private JPanel headerContainer;
    private JLabel title;
    private JLabel description;

    private JPanel contentContainer;

    private JTabbedPane tabbedPane;
    private GameplayTab gameplayTab;
    private AppearanceTab appearanceTab;
    private ControlsTab controlsTab;
    private AudioTab audioTab;

    private JPanel ctrlButtonsContainer;
    private JButton defaultsButton;
    private JButton cancelButton;
    private JButton saveButton;

    private ActionListener defaultsActionListener;
    private ActionListener cancelActionListener;
    private ActionListener saveActionListener;

    private boolean dirty = false;

    @Override
    public void init() {
        setLayout(new MigLayout("flowx, wrap, gapy 64, insets 0, al center center", "[grow, fill]"));

        headerContainer = new JPanel(new MigLayout("flowy, gapy 8, insets 0, al center center", "[grow, fill]"));
        title = new JLabel("SETTINGS");
        description = new JLabel("<html>Customize your experience of the game.</html>");

        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
        title.setHorizontalAlignment(JLabel.CENTER);
        description.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
        description.setHorizontalAlignment(JLabel.CENTER);

        headerContainer.add(title);
        headerContainer.add(description);

        contentContainer = new JPanel(new MigLayout("flowy, gapy 24px, insets 0, al center center", "[grow, fill]"));

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        gameplayTab = new GameplayTab();
        appearanceTab = new AppearanceTab();
        controlsTab = new ControlsTab();
        audioTab = new AudioTab();

        tabbedPane.addTab("Gameplay", gameplayTab);
        tabbedPane.addTab("Appearance", appearanceTab);
        tabbedPane.addTab("Controls", controlsTab);
        tabbedPane.addTab("Audio", audioTab);

        ctrlButtonsContainer = new JPanel(new MigLayout("flowx, insets 0, gapx 8px", "[]push[]8px[]"));
        defaultsButton = new JButton("Defaults",
                new SVGIconUIColor("reset-default.svg", 1, "foreground.muted"));
        cancelButton = new JButton("Cancel", new SVGIconUIColor("arrow-left.svg", 1, "foreground.muted"));
        saveButton = new JButton("Save & Apply", new SVGIconUIColor("save.svg", 1, "foreground.primary"));

        defaultsButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
        cancelButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "error");
        saveButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "primary");

        ctrlButtonsContainer.add(new JSeparator(JSeparator.HORIZONTAL), "cell 0 0 3, growx, gapbottom 16, wrap");
        ctrlButtonsContainer.add(cancelButton);
        ctrlButtonsContainer.add(defaultsButton);
        ctrlButtonsContainer.add(saveButton);

        add(headerContainer);
        add(contentContainer, "w ::720px, center");
        contentContainer.add(tabbedPane, "h 250px::");
        contentContainer.add(ctrlButtonsContainer);

        defaultsActionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                gameplayTab.chosenDifficulty = UserSettings.getInstance().getDEFAULT_DIFFICULTY();
                gameplayTab.difficultyBox.setSelectedItem(UserSettings.getInstance().getDEFAULT_DIFFICULTY());

                appearanceTab.dark = UserSettings.getInstance().getDEFAULT_THEME() == GameTheme.DARK;
                appearanceTab.themeToggle.setSelected(appearanceTab.dark);

                audioTab.newVolume = UserSettings.getInstance().getDEFAULT_VOLUME();
                audioTab.volumeSlider.setValue(audioTab.newVolume);

                dirty = true;
            }
        };

        cancelActionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (dirty) {
                    final int confirmed = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(appearanceTab),
                            "You have unsaved changes. Are you sure?");

                    if (confirmed != 0) {
                        return;
                    }
                }

                gameplayTab.chosenDifficulty = UserSettings.getInstance().getCurrentDifficulty();
                gameplayTab.difficultyBox.setSelectedItem(UserSettings.getInstance().getCurrentDifficulty());

                appearanceTab.dark = UserSettings.getInstance().getCurrentTheme() == GameTheme.DARK;
                appearanceTab.themeToggle.setSelected(appearanceTab.dark);

                audioTab.newVolume = UserSettings.getInstance().getVolume();
                audioTab.volumeSlider.setValue(audioTab.newVolume);

                dirty = false;

                Navigator.navigate(PageStartMenu.class);
            }
        };

        saveActionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int confirmed = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(appearanceTab),
                        "Are you sure you want to save?");

                if (confirmed != 0) {
                    return;
                }

                if (gameplayTab.chosenDifficulty != UserSettings.getInstance().getCurrentDifficulty()) {
                    UserSettings.getInstance().setDifficulty(gameplayTab.chosenDifficulty);
                }

                if (appearanceTab.dark) {
                    if (UserSettings.getInstance().getCurrentTheme() != GameTheme.DARK) {
                        UserSettings.getInstance().setTheme(GameTheme.DARK);
                    }
                } else {
                    if (UserSettings.getInstance().getCurrentTheme() != GameTheme.LIGHT) {
                        UserSettings.getInstance().setTheme(GameTheme.LIGHT);
                    }
                }

                if (audioTab.newVolume != UserSettings.getInstance().getVolume()) {
                    UserSettings.getInstance().setVolume(audioTab.newVolume);
                }

                dirty = false;
            }
        };
    }

    @Override
    public void open() {
        defaultsButton.addActionListener(defaultsActionListener);
        cancelButton.addActionListener(cancelActionListener);
        saveButton.addActionListener(saveActionListener);

        gameplayTab.open();
        appearanceTab.open();
        controlsTab.open();
        audioTab.open();
    }

    @Override
    public void close() {
        defaultsButton.removeActionListener(defaultsActionListener);
        cancelButton.removeActionListener(cancelActionListener);
        saveButton.removeActionListener(saveActionListener);

        gameplayTab.close();
        appearanceTab.close();
        controlsTab.close();
        audioTab.close();
    }

    private void addSectionHeader(final JPanel panel, final String text) {
        final JLabel label = new JLabel(text);
        label.putClientProperty("FlatLaf.styleClass", "h4"); // Use FlatLaf typography
        label.setForeground(UIManager.getColor("Accent.color")); // Use theme accent color

        panel.add(label, "span, wrap, gaptop 10");
    }

    private class GameplayTab extends JPanel {
        final JComboBox<GameDifficulty> difficultyBox;
        final ItemListener difficultyItemListener;
        GameDifficulty chosenDifficulty;

        GameplayTab() {
            setLayout(new MigLayout("fillx, wrap 2, insets 16 0 0 0", "[fill][right]", "[]16[]"));

            addSectionHeader(this, "General Rules");

            difficultyBox = new JComboBox<>(GameDifficulty.values());
            difficultyItemListener = new ItemListener() {
                @Override
                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        final GameDifficulty selected = (GameDifficulty) e.getItem();

                        chosenDifficulty = selected;
                        dirty = true;
                    }
                }
            };

            difficultyBox.setSelectedItem(UserSettings.getInstance().getCurrentDifficulty());
            difficultyBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
                        final boolean isSelected, final boolean cellHasFocus) {

                    if (value instanceof final GameDifficulty gameDifficulty) {
                        setText(gameDifficulty.toDetailedString());
                    }

                    return this;
                }
            });

            chosenDifficulty = UserSettings.getInstance().getCurrentDifficulty();

            add(new JLabel("Difficulty:"));
            add(difficultyBox);
        }

        void open() {
            difficultyBox.addItemListener(difficultyItemListener);
        }

        void close() {
            difficultyBox.removeItemListener(difficultyItemListener);
        }
    }

    private class AppearanceTab extends JPanel {
        JToggleButton themeToggle;
        ActionListener themeToggleActionListener;
        boolean dark;

        AppearanceTab() {
            setLayout(new MigLayout("fillx, wrap 2, insets 16 0 0 0", "[fill][right]", "[]16[]"));

            addSectionHeader(this, "Theme & Style");

            add(new JLabel("App Theme: "));
            dark = UserSettings.getInstance().getCurrentTheme() == GameTheme.DARK;
            themeToggle = new JToggleButton("Dark", new SVGIconUIColor("moon.svg", 1, "foreground.background"));

            if (dark) {
                themeToggle.setSelected(true);
            }

            themeToggleActionListener = new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (themeToggle.isSelected()) {
                        dark = true;
                    } else {
                        dark = false;
                    }

                    dirty = true;
                }
            };

            add(themeToggle);
        }

        void open() {
            themeToggle.addActionListener(themeToggleActionListener);
        }

        void close() {
            themeToggle.removeActionListener(themeToggleActionListener);
        }
    }

    private class ControlsTab extends JPanel {

        void open() {
        }

        void close() {
        }
    }

    private class AudioTab extends JPanel {
        JSlider volumeSlider;
        ChangeListener volumeChangeListener;

        int newVolume = 0;

        AudioTab() {
            setLayout(new MigLayout("fillx, wrap 2, insets 16 0 0 0", "[fill][right]", "[]16[]"));

            addSectionHeader(this, "Volume");

            add(new JLabel("Master Volume"));

            volumeSlider = new JSlider(0, 100, UserSettings.getInstance().getVolume());
            newVolume = volumeSlider.getValue();
            volumeChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    final JSlider source = (JSlider) e.getSource();

                    if (!source.getValueIsAdjusting()) {
                        newVolume = (int) source.getValue();

                        dirty = true;
                    }
                }
            };

            add(volumeSlider);
        }

        void open() {
            volumeSlider.addChangeListener(volumeChangeListener);
        }

        void close() {
            volumeSlider.removeChangeListener(volumeChangeListener);
        }
    }
}
