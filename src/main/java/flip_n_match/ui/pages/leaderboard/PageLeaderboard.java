package flip_n_match.ui.pages.leaderboard;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.audio.AudioManager;
import flip_n_match.game.settings.GameDifficulty;
import flip_n_match.lib.Scorer;
import flip_n_match.ui.buttons.AudioButtonWrapper;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.system.Page;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class PageLeaderboard extends Page {
    private AudioButtonWrapper backBtnWrapper;
    private JTextField searchField;
    private JTabbedPane tabbedPane;

    private final List<DifficultyTab> difficultyTabs = new ArrayList<>();
    private final ActionListener backBtnListener = new BackToMenuListener();

    private ChangeListener tabClickListener;
    private MouseMotionAdapter tabHoverListener;
    private MouseAdapter tabExitedListener;
    private int lastHoveredTabIndex = -1;

    @Override
    public void init() {
        setLayout(new MigLayout("al center center, fillx, wrap 1", "[grow]", "[]16px[]64px[grow]"));

        JLabel title = new JLabel("LEADERBOARD");
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new MigLayout("flowx, insets 0", "[]push[]push[]", "[center]"));

        backBtnWrapper = new AudioButtonWrapper("Back", new SVGIconUIColor("arrow-left.svg", 0.75f, "foreground.primary"), () -> backBtnListener.actionPerformed(null));
        JButton backButton = backBtnWrapper.getButton();

        backButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "primary");

        headerPanel.add(backButton);
        headerPanel.add(title, "gapleft 24px");
        headerPanel.add(new JLabel(""));

        JPanel searchPanel = new JPanel(new MigLayout("insets 0, al left center", "[grow]"));
        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search player by name...");
        searchPanel.add(searchField, "growx");

        tabbedPane = new JTabbedPane();
        TopThreeBoldRenderer renderer = new TopThreeBoldRenderer();

        GameDifficulty[] difficulties = GameDifficulty.class.getEnumConstants();

        if (difficulties != null) {
            for (GameDifficulty difficultyEnum : difficulties) {
                String difficultyName = difficultyEnum.name();
                DifficultyTab tab = new DifficultyTab(difficultyName, renderer);
                difficultyTabs.add(tab);
                tabbedPane.addTab(difficultyName, tab.getContentPanel());

                LeaderboardSearchListener searchListener = new LeaderboardSearchListener(searchField, tab.getSorter(), 1);
                searchField.getDocument().addDocumentListener(searchListener);
            }
        }

        tabClickListener = e -> AudioManager.getInstance().playSfx(AudioManager.Sfx.BUTTON_CLICK);

        tabHoverListener = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int currentHover = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (currentHover != lastHoveredTabIndex) {
                    lastHoveredTabIndex = currentHover;
                    if (currentHover != -1) {
                        AudioManager.getInstance().playSfx(AudioManager.Sfx.BUTTON_HOVER);
                    }
                }
            }
        };

        tabExitedListener = new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                lastHoveredTabIndex = -1;
            }
        };

        add(headerPanel, "growx");
        //add(searchPanel, "growx, wmax 400px, al center");
        add(tabbedPane, "grow, h 400px!");
    }

    @Override
    public void open() {
        backBtnWrapper.bind();

        tabbedPane.addChangeListener(tabClickListener);
        tabbedPane.addMouseMotionListener(tabHoverListener);
        tabbedPane.addMouseListener(tabExitedListener);

        SwingUtilities.invokeLater(() -> {
            for (DifficultyTab tab : difficultyTabs) {
                List<Scorer.ScoreEntry> scores = Scorer.getSortedScores(tab.getDifficulty());
                tab.updateData(scores);
            }
        });
    }

    @Override
    public void close() {
        backBtnWrapper.unbind();

        tabbedPane.removeChangeListener(tabClickListener);
        tabbedPane.removeMouseMotionListener(tabHoverListener);
        tabbedPane.removeMouseListener(tabExitedListener);

        searchField.setText("");
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private static class DifficultyTab {
        @Getter
        private final String difficulty;
        @Getter
        private final JPanel contentPanel;
        private final CardLayout cardLayout;

        private final LeaderboardTableModel tableModel;
        @Getter
        private final TableRowSorter<LeaderboardTableModel> sorter;

        public DifficultyTab(String difficulty, TopThreeBoldRenderer renderer) {
            this.difficulty = difficulty;
            this.cardLayout = new CardLayout();
            this.contentPanel = new JPanel(cardLayout);

            tableModel = new LeaderboardTableModel();
            JTable table = new JTable(tableModel);
            table.getTableHeader().setReorderingAllowed(false);

            sorter = new TableRowSorter<>(tableModel);
            table.setRowSorter(sorter);

            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }

            JScrollPane scroller = new JScrollPane(table);

            JLabel emptyLabel = new JLabel("<html>No player has played in this difficulty yet. Play now to see your ranking.</html>");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
            emptyLabel.setForeground(UIManager.getColor("Label.disabledForeground"));

            contentPanel.add(scroller, "TABLE_VIEW");
            contentPanel.add(emptyLabel, "EMPTY_VIEW");
        }

        public void updateData(List<Scorer.ScoreEntry> scores) {
            if (scores == null || scores.isEmpty()) {
                cardLayout.show(contentPanel, "EMPTY_VIEW");
            } else {
                tableModel.setScores(scores);
                cardLayout.show(contentPanel, "TABLE_VIEW");
            }
        }
    }
}