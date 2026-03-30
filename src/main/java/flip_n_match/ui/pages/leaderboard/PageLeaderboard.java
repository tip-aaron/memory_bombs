package flip_n_match.ui.pages.leaderboard;

import com.formdev.flatlaf.FlatClientProperties;
import flip_n_match.game.settings.GameDifficulty;
import flip_n_match.lib.Scorer;
import flip_n_match.ui.icons.SVGIconUIColor;
import flip_n_match.ui.system.Page;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PageLeaderboard extends Page {
    private JButton backToMainMenuBtn;
    private JTextField searchField;

    // List to keep track of our generated tabs
    private final List<DifficultyTab> difficultyTabs = new ArrayList<>();

    private final ActionListener backBtnListener = new BackToMenuListener();

    @Override
    public void init() {
        setLayout(new MigLayout("al center center, fillx, wrap 1", "[grow]", "[]16px[]64px[grow]"));

        JLabel title = new JLabel("LEADERBOARD");
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h00");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new MigLayout("flowx, insets 0", "[]push[]push[]", "[center]"));
        backToMainMenuBtn = new JButton("Back", new SVGIconUIColor("arrow-left.svg", 0.75f, "foreground.primary"));
        backToMainMenuBtn.putClientProperty(FlatClientProperties.STYLE_CLASS, "primary");

        headerPanel.add(backToMainMenuBtn);
        headerPanel.add(title, "gapleft 24px");
        headerPanel.add(new JLabel(""));

        JPanel searchPanel = new JPanel(new MigLayout("insets 0, al left center", "[grow]"));
        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search player by name...");
        searchPanel.add(searchField, "growx");

        JTabbedPane tabbedPane = new JTabbedPane();
        TopThreeBoldRenderer renderer = new TopThreeBoldRenderer();

        GameDifficulty[] difficulties = GameDifficulty.class.getEnumConstants();

        if (difficulties != null) {
            for (GameDifficulty difficultyEnum : difficulties) {
                String difficultyName = difficultyEnum.name();
                DifficultyTab tab = new DifficultyTab(difficultyName, renderer);
                difficultyTabs.add(tab);
                tabbedPane.addTab(difficultyName, tab.getContentPanel());

                // Hook up the search listener to each tab's sorter
                LeaderboardSearchListener searchListener = new LeaderboardSearchListener(searchField, tab.getSorter(), 1);
                searchField.getDocument().addDocumentListener(searchListener);
            }
        }

        add(headerPanel, "growx");
        //add(searchPanel, "growx, wmax 400px, al center");
        add(tabbedPane, "grow, h 400px!");
    }

    @Override
    public void open() {
        backToMainMenuBtn.addActionListener(backBtnListener);

        SwingUtilities.invokeLater(() -> {
            // Refresh data for each tab dynamically
            for (DifficultyTab tab : difficultyTabs) {
                List<Scorer.ScoreEntry> scores = Scorer.getSortedScores(tab.getDifficulty());
                tab.updateData(scores);
            }
        });
    }

    @Override
    public void close() {
        backToMainMenuBtn.removeActionListener(backBtnListener);
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