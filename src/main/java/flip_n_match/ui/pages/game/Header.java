package flip_n_match.ui.pages.game;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.game.settings.UserSettings;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.pages.PageGameMenu;
import flip_n_match.ui.system.Navigator;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Header extends JPanel {
    private final JLabel stopwatchText;
    private final JLabel difficultyText;
    private final JLabel mineCountText;
    private final JLabel tilesFlaggedText;
    private final JButton menuBtn;
    private final ActionListener menuBtnListener = ignored -> Navigator.navigate(PageGameMenu.class);

    public Header() {
        setLayout(new MigLayout("insets 0, flowx, al center center", "[33%, left]push[34%, center]push[33%, right]"));

        JPanel middle = new JPanel(new MigLayout("insets 0, flowx, al center center"));

        stopwatchText = new JLabel("00:00.000", new SVGIconUIColor("timer.svg", 1f, "foreground.background"), JLabel.LEFT);
        difficultyText = new JLabel("Difficulty: Easy");
        mineCountText = new JLabel("Mines Left: N/A");
        tilesFlaggedText = new JLabel("Flagged: N/A");
        menuBtn = new JButton(new SVGIconUIColor("menu.svg", 0.75f, "foreground.primary"));

        stopwatchText.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        stopwatchText.setHorizontalAlignment(JLabel.CENTER);

        add(difficultyText, "gapright 32px");
        add(middle, "grow");
        middle.add(stopwatchText, "wrap 1, gapy 24px, span, center");
        middle.add(mineCountText, "gapright 8px, split 2, center");
        middle.add(tilesFlaggedText, "");
        add(menuBtn, "gapleft 32px");
    }

    void setStopwatchText(String text) {
        stopwatchText.setText(text);
    }

    public void updateMineStats(int flagged, int totalMines) {
        tilesFlaggedText.setText("Flagged: " + flagged);
        mineCountText.setText("Mines Left: " + (totalMines - flagged));
    }

    void open() {
        UserSettings settings = UserSettings.getInstance();

        difficultyText.setText("Difficulty: " + settings.getGameplay().difficulty().get().name());

        int totalMines = settings.getGameplay().difficulty().get().getMineCount();
        updateMineStats(0, totalMines);

        menuBtn.addActionListener(menuBtnListener);
    }

    void close() {
        menuBtn.removeActionListener(menuBtnListener);
    }

    void destroy() {
        mineCountText.setText("Mines Left: ");
        tilesFlaggedText.setText("Flagged: ");
    }
}