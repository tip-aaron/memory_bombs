package flip_n_match.ui.pages.settings;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class ASettingsTab extends JPanel {
    @Setter
    private Runnable dirtyListener;

    public ASettingsTab() {
        setLayout(new MigLayout("fillx, wrap 2, insets 16 0 0 0", "[fill][right]", "[]16[]"));
    }

    protected void notifyDirty() {
        if (dirtyListener != null) {
            dirtyListener.run();
        }
    }

    protected void addSectionHeader(@NotNull String titleText, String descriptionText) {
        JPanel container = new JPanel(new MigLayout("fillx, wrap 1, insets 0", "[]", "[]4[]"));
        JLabel title = new JLabel(titleText);

        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        container.add(title);

        if (descriptionText != null && !descriptionText.isEmpty()) {
            JLabel description = new JLabel("<html>" + descriptionText + "</html>");

            description.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted small");

            container.add(description);
        }

        add(container, "span, wrap, gaptop 8, gapbottom 4");
    }

    public abstract void open();
    public abstract void close();
    public abstract boolean isDirty();
    public abstract void applyChanges();
    public abstract void revertChanges();
    public abstract void loadDefaults();
}
