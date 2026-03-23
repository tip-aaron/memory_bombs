package flip_n_match.ui.pages;

import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatClientProperties;

import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.pages.game.PageGameMain;
import flip_n_match.ui.system.AllPages;
import flip_n_match.ui.system.Navigator;
import flip_n_match.ui.system.Page;
import net.miginfocom.swing.MigLayout;

public class PageGameMenu extends Page {
    private JPanel headerContainer;

    private JButton resumeButton;
    private JButton exitButton;

    private ActionListener resumeActionListener;
    private ActionListener exitActionListener;

    @Override
    public void init() {
        // Center everything on the screen
        setLayout(new MigLayout("flowx, wrap, gapy 64, insets 0, al center center", "[grow, fill]"));

        // Header Section (Title)
        headerContainer = new JPanel(new MigLayout("flowy, gapy 8, insets 0, al center center", "[grow, fill]"));
        JLabel title = new JLabel("MENU");

        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
        title.setHorizontalAlignment(JLabel.CENTER);

        headerContainer.add(title);

        // Buttons Section
        JPanel buttonsContainer = new JPanel(
                new MigLayout("flowx, wrap, insets 0, gap 16, al center center", "[grow, fill, ::320px]"));

        // Reusing the SVGIconUIColor from your template, feel free to change the file names if you have specific icons
        resumeButton = new JButton("Resume", new SVGIconUIColor("play.svg", 1, "foreground.background"));
        exitButton = new JButton("Back to Main Menu", new SVGIconUIColor("logout.svg", 1, "foreground.background"));

        resumeButton.setHorizontalAlignment(JButton.CENTER);
        exitButton.setHorizontalAlignment(JButton.CENTER);

        buttonsContainer.add(resumeButton);
        buttonsContainer.add(exitButton);

        add(headerContainer, "grow");
        add(buttonsContainer, "grow");

        // Action Listeners
        resumeActionListener = e -> Navigator.navigate(PageGameMain.class);

        exitActionListener = e -> {
            final int res = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Are you sure you want to exit? Your progress will not be saved.",
                    "Exit Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            // 0 corresponds to JOptionPane.YES_OPTION
            if (res == JOptionPane.YES_OPTION) {
                Navigator.navigate(PageStartMenu.class);
                AllPages.getPage(PageGameMain.class).destroy();
                AllPages.removePage(PageGameMain.class);
            }
        };
    }

    @Override
    public void open() {
        resumeButton.addActionListener(resumeActionListener);
        exitButton.addActionListener(exitActionListener);
    }

    @Override
    public void close() {
        resumeButton.removeActionListener(resumeActionListener);
        exitButton.removeActionListener(exitActionListener);
    }
}