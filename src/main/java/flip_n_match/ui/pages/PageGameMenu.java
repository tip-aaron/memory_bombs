package flip_n_match.ui.pages;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.ui.buttons.AudioButtonWrapper;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.pages.game.PageGameMain;
import flip_n_match.ui.pages.settings.PageSettings;
import flip_n_match.ui.system.AllPages;
import flip_n_match.ui.system.Navigator;
import flip_n_match.ui.system.Page;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class PageGameMenu extends Page {

    private AudioButtonWrapper resumeBtnWrapper;
    private AudioButtonWrapper settingsBtnWrapper;
    private AudioButtonWrapper exitBtnWrapper;

    @Override
    public void init() {
        setLayout(new MigLayout("flowx, wrap, gapy 64, insets 0, al center center", "[grow, fill]"));

        // Header Section (Title)
        JPanel headerContainer = new JPanel(new MigLayout("flowy, gapy 8, insets 0, al center center", "[grow, fill]"));
        JLabel title = new JLabel("PAUSED");

        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
        title.setHorizontalAlignment(JLabel.CENTER);

        headerContainer.add(title);

        JPanel buttonsContainer = new JPanel(
                new MigLayout("flowx, wrap, insets 0, gap 16, al center center", "[grow, fill, ::320px]"));

        resumeBtnWrapper = new AudioButtonWrapper("Resume", new SVGIconUIColor("play.svg", 1, "foreground.background"), () -> Navigator.navigate(PageGameMain.class));

        settingsBtnWrapper = new AudioButtonWrapper("Settings", new SVGIconUIColor("settings.svg", 1, "foreground.background"), () -> {
            PageSettings.previousPage = PageGameMenu.class;
            Navigator.navigate(PageSettings.class);
        });

        exitBtnWrapper = new AudioButtonWrapper("Back to Main Menu", new SVGIconUIColor("logout.svg", 1, "foreground.background"), () -> {
            final int res = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Are you sure you want to exit? Your progress will not be saved.",
                    "Exit Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                Navigator.navigate(PageStartMenu.class);
                AllPages.getPage(PageGameMain.class).destroy();
                AllPages.removePage(PageGameMain.class);
            }
        });

        JButton resumeButton = resumeBtnWrapper.getButton();
        JButton settingsButton = settingsBtnWrapper.getButton();
        JButton exitButton = exitBtnWrapper.getButton();

        resumeButton.setHorizontalAlignment(JButton.CENTER);
        settingsButton.setHorizontalAlignment(JButton.CENTER);
        exitButton.setHorizontalAlignment(JButton.CENTER);

        buttonsContainer.add(resumeButton);
        buttonsContainer.add(settingsButton);
        buttonsContainer.add(exitButton);

        add(headerContainer, "grow");
        add(buttonsContainer, "grow");
    }

    @Override
    public void open() {
        resumeBtnWrapper.bind();
        settingsBtnWrapper.bind();
        exitBtnWrapper.bind();
    }

    @Override
    public void close() {
        resumeBtnWrapper.unbind();
        settingsBtnWrapper.unbind();
        exitBtnWrapper.unbind();
    }
}