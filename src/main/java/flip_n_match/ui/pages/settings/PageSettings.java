package flip_n_match.ui.pages.settings;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.pages.PageStartMenu;
import flip_n_match.ui.system.Navigator;
import flip_n_match.ui.system.Page;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class PageSettings extends Page {
    private ASettingsTab[] tabs;
    private JButton defaultsButton;
    private JButton cancelButton;
    private JButton saveButton;
    private ActionListener defaultsButtonActionListener;
    private ActionListener cancelButtonActionListener;
    private ActionListener saveButtonActionListener;
    private boolean isDirty = false;

    @Override
    public void init() {
        setLayout(new MigLayout("flowx, wrap, gapy 64, insets 0, al center center", "[grow, fill]"));

        JPanel headerContainer = getHeaderContainer();
        JPanel contentContainer = new JPanel(new MigLayout("flowy, insets 0, al center center", "[grow, fill]"));
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        tabs = new ASettingsTab[4];
        tabs[0] = new GameplayTab();
        tabs[1] = new AppearanceTab();
        tabs[2] = new ControlsTab();
        tabs[3] = new AudioTab();

        Runnable dirtyCheckListener = this::checkDirtyState;

        for (int i = 0; i < 4; ++i) {
            ASettingsTab tab = tabs[i];
            JScrollPane scroller = new JScrollPane(tab);

            scroller.setBorder(null);

            tab.setDirtyListener(dirtyCheckListener);

            scroller.setBorder(null);

            tabbedPane.addTab(tab.getClass().getAnnotation(TabInfo.class).name(), scroller);
        }

        JPanel ctrlButtonsContainer = new JPanel(new MigLayout("flowx, insets 0, gapx 8px", "[]push[]16px[]"));
        defaultsButton = new JButton("Defaults", new SVGIconUIColor("reset-default.svg", 1, "foreground.muted"));
        cancelButton = new JButton("Cancel", new SVGIconUIColor("arrow-left.svg", 1, "foreground.muted"));
        saveButton = new JButton("Save & Apply", new SVGIconUIColor("save.svg", 1, "foreground.primary"));

        defaultsButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
        cancelButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "error");
        saveButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "primary");
        saveButton.setEnabled(false); // Initially disabled

        ctrlButtonsContainer.add(cancelButton, "gapright 24px");
        ctrlButtonsContainer.add(defaultsButton);
        ctrlButtonsContainer.add(saveButton);

        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setMinimumSize(new Dimension(separator.getMinimumSize().width, 8));

        add(headerContainer);
        add(contentContainer, "w ::720px, center");
        contentContainer.add(tabbedPane, "h 300px::, gapbottom 24px");
        contentContainer.add(separator, "spanx, growx, gapbottom 16px");
        contentContainer.add(ctrlButtonsContainer);

        defaultsButtonActionListener = new DefaultsButtonActionListener();
        cancelButtonActionListener = new CancelButtonActionListener();
        saveButtonActionListener = new SaveButtonActionListener();
    }

    @Override
    public void open() {
        for (ASettingsTab tab : tabs) {
            tab.open();
            tab.revertChanges();
        }

        defaultsButton.addActionListener(defaultsButtonActionListener);
        cancelButton.addActionListener(cancelButtonActionListener);
        saveButton.addActionListener(saveButtonActionListener);

        checkDirtyState();
    }

    @Override
    public void close() {
        for (ASettingsTab tab : tabs) {
            tab.close();
        }

        defaultsButton.removeActionListener(defaultsButtonActionListener);
        cancelButton.removeActionListener(cancelButtonActionListener);
        saveButton.removeActionListener(saveButtonActionListener);
    }

    private void checkDirtyState() {
        boolean anyDirty = Arrays.stream(tabs).anyMatch(ASettingsTab::isDirty);

        if (this.isDirty != anyDirty) {
            this.isDirty = anyDirty;

            saveButton.setEnabled(this.isDirty);
        }
    }

    private JPanel getHeaderContainer() {
        JPanel headerContainer = new JPanel(new MigLayout("flowy, gapy 8, insets 0, al center center", "[grow, fill, center]"));
        JLabel title = new JLabel("SETTINGS");
        JLabel description = new JLabel("<html>Customize your experience of the game.</html>");

        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
        title.setHorizontalAlignment(JLabel.CENTER);
        description.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
        description.setHorizontalAlignment(JLabel.CENTER);

        headerContainer.add(title);
        headerContainer.add(description);

        return headerContainer;
    }

    class DefaultsButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (ASettingsTab tab : tabs) {
                tab.loadDefaults();
            }

            checkDirtyState();
        }
    }

    class CancelButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isDirty) {
                int confirmed = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(cancelButton),
                        "You have unsaved changes. Are you sure you want to discard them?", "Unsaved Changes", JOptionPane.YES_NO_OPTION);

                if (confirmed != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            for (ASettingsTab tab : tabs) {
                tab.revertChanges();
            }

            checkDirtyState();

            Navigator.navigate(PageStartMenu.class);
        }
    }

    class SaveButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int confirmed = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(saveButton),
                    "Are you sure you want to save?", "Confirm Save", JOptionPane.YES_NO_OPTION);

            if (confirmed == JOptionPane.YES_OPTION) {
                for (ASettingsTab tab : tabs) {
                    tab.applyChanges();
                }

                checkDirtyState();
            }
        }
    }
}
