package flip_n_match.ui.pages;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.App;
import flip_n_match.constants.Metadata;
import flip_n_match.ui.buttons.AudioButtonWrapper;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.pages.game.PageGameMain;
import flip_n_match.ui.pages.leaderboard.PageLeaderboard;
import flip_n_match.ui.pages.settings.PageSettings;
import flip_n_match.ui.system.Navigator;
import flip_n_match.ui.system.Page;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Locale;

public class PageStartMenu extends Page {
    private JPanel headerContainer;

    // Use our wrappers instead of raw JButtons and ActionListeners
    private AudioButtonWrapper playBtnWrapper;
    private AudioButtonWrapper settingsBtnWrapper;
    private AudioButtonWrapper leaderboardBtnWrapper;
    private AudioButtonWrapper exitBtnWrapper;

    @Override
    public void init() {
        setLayout(new MigLayout("flowx, wrap, gapy 64, insets 0, al center center", "[grow, fill]"));

        headerContainer = new JPanel(new MigLayout("flowy, gapy 8, insets 0, al center center", "[grow, fill]"));
        JLabel title = new JLabel(Metadata.APP_TITLE.toUpperCase(Locale.ENGLISH));
        JLabel description = new JLabel("<html>Flip the cards and match the numbers... or go <b>BOOM</b>!</html>");

        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
        title.setHorizontalAlignment(JLabel.CENTER);
        description.putClientProperty(FlatClientProperties.STYLE_CLASS, "muted");
        description.setHorizontalAlignment(JLabel.CENTER);

        headerContainer.add(title);
        headerContainer.add(description);

        JPanel buttonsContainer = new JPanel(
                new MigLayout("flowx, wrap, insets 0, gap 16, al center center", "[grow, fill, ::320px]"));

        // Initialize Wrappers with their respective icons and click actions
        playBtnWrapper = new AudioButtonWrapper("Play", new SVGIconUIColor("play.svg", 1, "foreground.background"), () -> Navigator.navigate(PageGameMain.class));

        settingsBtnWrapper = new AudioButtonWrapper("Settings", new SVGIconUIColor("settings.svg", 1, "foreground.background"), () -> {
            PageSettings.previousPage = PageStartMenu.class;
            Navigator.navigate(PageSettings.class);
        });
        leaderboardBtnWrapper = new AudioButtonWrapper("Leaderboards", new SVGIconUIColor("trophy.svg", 1, "foreground.background"), () -> Navigator.navigate(PageLeaderboard.class));

        exitBtnWrapper = new AudioButtonWrapper("Quit", new SVGIconUIColor("logout.svg", 1, "foreground.background"), () -> {
            final int res = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(headerContainer),
                    "Are you sure you want to exit the game?",
                    "Quit Game",
                    JOptionPane.YES_NO_OPTION
            );

            if (res == JOptionPane.YES_OPTION) {
                App.close();
            }
        });

        // Extract the actual JButtons for layout and styling
        JButton playButton = playBtnWrapper.getButton();
        JButton settingsButton = settingsBtnWrapper.getButton();
        JButton leaderboardBtn = leaderboardBtnWrapper.getButton();
        JButton exitButton = exitBtnWrapper.getButton();

        playButton.setHorizontalAlignment(JButton.CENTER);
        settingsButton.setHorizontalAlignment(JButton.CENTER);
        leaderboardBtn.setHorizontalAlignment(JButton.CENTER);
        exitButton.setHorizontalAlignment(JButton.CENTER);

        buttonsContainer.add(playButton);
        buttonsContainer.add(leaderboardBtn);
        buttonsContainer.add(settingsButton);
        buttonsContainer.add(exitButton);

        add(headerContainer, "grow");
        add(buttonsContainer, "grow");
    }

    @Override
    public void open() {
        // Bind hover and click events
        playBtnWrapper.bind();
        settingsBtnWrapper.bind();
        leaderboardBtnWrapper.bind();
        exitBtnWrapper.bind();
    }

    @Override
    public void close() {
        // Unbind events to clean up memory
        playBtnWrapper.unbind();
        settingsBtnWrapper.unbind();
        leaderboardBtnWrapper.unbind();
        exitBtnWrapper.unbind();
    }
}