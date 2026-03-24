package flip_n_match.ui.pages.settings;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;

@TabInfo(name = "Controls")
public class ControlsTab extends ASettingsTab {
    public ControlsTab() {
        super();

        addSectionHeader("Game Controls", "Standard controls for Flip 'n Match");

        addControlBind("Reveal / Flip Tile", "Left Click");
        addControlBind("Flag Mine", "Right Click");
        addControlBind("Chord (Reveal Adjacent)", "Middle Click / L+R Click");
        addControlBind("Pause Game / Menu", "ESC");
    }

    private void addControlBind(String actionText, String keyBind) {
        JLabel actionLabel = new JLabel(actionText);
        JLabel bindLabel = new JLabel(keyBind);

        bindLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "monospaced");
        bindLabel.setHorizontalAlignment(JLabel.RIGHT);

        add(actionLabel);
        add(bindLabel);
    }

    @Override public void open() {}
    @Override public void close() {}
    @Override public boolean isDirty() { return false; }
    @Override public void applyChanges() {}
    @Override public void revertChanges() {}
    @Override public void loadDefaults() {}
}
