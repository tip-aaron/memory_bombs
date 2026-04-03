package flip_n_match.ui.system;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.audio.AudioManager;
import flip_n_match.ui.icons.SVGIconUIColor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class RootPage extends JPanel implements AudioManager.MusicObserver {
    private final JPanel mainPanel;
    private final JLabel musicStatusLabel;
    private final JPanel footerPanel;

    public RootPage() {
        // Changed layout to 1 column, 2 rows: [grow] for content, [40!] fixed for footer
        setLayout(new MigLayout("fill, insets 0, gap 0", "[grow]", "[grow][40!]"));

        mainPanel = new JPanel(new MigLayout("fill, insets 16, gap 0", "[grow]", "[grow]"));

        // --- Footer Setup ---
        footerPanel = new JPanel(new MigLayout("fill, insets 0 20", "[grow][right]", "center"));
        footerPanel.putClientProperty(FlatClientProperties.STYLE_CLASS, "footer");
        footerPanel.setBackground(UIManager.getColor("Panel.background").darker());

        musicStatusLabel = new JLabel("No music playing", new SVGIconUIColor("note.svg", 0.5f, "foreground.background"), JLabel.LEFT);
        musicStatusLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "mini muted");

        footerPanel.add(musicStatusLabel, "growx");

        add(mainPanel, "grow, wrap");
        add(footerPanel, "growx, south");

        AudioManager.getInstance().addMusicObserver(this);
    }

    public void setPage(final Page page) {
        SwingUtilities.invokeLater(() -> {
            mainPanel.removeAll();
            mainPanel.add(page, "grow");
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }

    private String getPrettyName(AudioManager.Music track) {
        String prettyName = track.name().replace("_", " ").toLowerCase();

        return prettyName.substring(0, 1).toUpperCase() + prettyName.substring(1);
    }

    @Override
    public void onMusicLoading(AudioManager.Music track) {
        SwingUtilities.invokeLater(() -> {
            musicStatusLabel.setText("Loading: " + getPrettyName(track) + "...");
            footerPanel.setVisible(true);
        });
    }

    @Override
    public void onMusicStarted(AudioManager.Music track) {
        SwingUtilities.invokeLater(() -> {
            musicStatusLabel.setText("Now Playing: " + getPrettyName(track));
            footerPanel.setVisible(true);
        });
    }

    @Override
    public void onMusicStopped() {
        SwingUtilities.invokeLater(() -> musicStatusLabel.setText("No music playing"));
    }
}