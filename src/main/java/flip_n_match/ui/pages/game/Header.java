package flip_n_match.ui.pages.game;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;

import flip_n_match.config.UserSettings;
import flip_n_match.constants.Metadata;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.pages.PageGameMenu;
import flip_n_match.ui.system.Navigator;
import net.miginfocom.swing.MigLayout;

import java.awt.event.ActionListener;

public class Header extends JPanel {
    private final JLabel stopwatchText;
    private final JLabel difficultyText;
    private final JLabel mineCountText;
    private final JButton menuBtn;
    private final ActionListener menuBtnListener = ignored -> Navigator.navigate(PageGameMenu.class);

    public Header() {
        setLayout(new MigLayout("insets 0, flowx, al center center", "[]push[][][]push[]"));

        JLabel titleText = new JLabel(Metadata.APP_TITLE);
        stopwatchText = new JLabel("00:00.000", new SVGIconUIColor("timer.svg", 0.75f, "foreground.background"),
                JLabel.LEFT);
        difficultyText = new JLabel("Difficulty: Easy");
        mineCountText = new JLabel("Mines: N/A");
        menuBtn = new JButton(new SVGIconUIColor("menu.svg", 0.75f, "foreground.primary"));

        titleText.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        menuBtn.putClientProperty(FlatClientProperties.STYLE_CLASS, "primary");

        add(titleText, "");
        add(difficultyText, "gapx 16px");
        add(stopwatchText, "gapx 16px");
        add(mineCountText, "gapx 16px");
        add(menuBtn, "");
    }

    void setStopwatchText(String text) {
        stopwatchText.setText(text);
    }


    void open() {
        UserSettings settings = UserSettings.getInstance();

        difficultyText.setText("Difficulty: " + settings.getCurrentDifficulty().name());
        mineCountText.setText("Mine Count: " + settings.getCurrentDifficulty().getMineCount());

        menuBtn.addActionListener(menuBtnListener);
    }

    void close() {
        menuBtn.removeActionListener(menuBtnListener);
    }

    void destroy() {
        mineCountText.setText("Mine Count: ");
    }
}
