package flip_n_match.ui.pages.settings;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public abstract class ASettingsTab extends JPanel {
    @Setter
    private Runnable dirtyListener;

    public ASettingsTab() {
        setLayout(new MigLayout("fillx, wrap 2, insets 16", "16 [grow][right]", "[top]24[top]"));
    }

    protected void notifyDirty() {
        if (dirtyListener != null) {
            dirtyListener.run();
        }
    }

    protected void addSectionHeader(@NotNull String titleText, String descriptionText) {
        JPanel container = new JPanel(new MigLayout("insets 16 0", "[grow, fill, left]"));
        JLabel title = new JLabel(titleText);

        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        container.add(title, "wrap, gapbottom 4px");

        if (descriptionText != null && !descriptionText.isEmpty()) {
            JLabel description = new JLabel("<html>" + descriptionText + "</html>");

            description.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
            description.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted small");

            container.add(description);
        }

        add(container, "span, wrap, gaptop 16, gapbottom 16");
    }

    public abstract void open();
    public abstract void close();

    public void destroy() {
        // Does nothing
    }
    public abstract boolean isDirty();
    public abstract void applyChanges();
    public abstract void revertChanges();
    public abstract void loadDefaults();
}
