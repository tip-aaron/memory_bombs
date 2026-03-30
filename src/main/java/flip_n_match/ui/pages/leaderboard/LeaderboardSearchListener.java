package flip_n_match.ui.pages.leaderboard;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

public class LeaderboardSearchListener implements DocumentListener {
    private final JTextField searchField;
    private final TableRowSorter<?> sorter;
    private final int searchColumnIndex;

    public LeaderboardSearchListener(JTextField searchField, TableRowSorter<?> sorter, int searchColumnIndex) {
        this.searchField = searchField;
        this.sorter = sorter;
        this.searchColumnIndex = searchColumnIndex;
    }

    private void filter() {
        String text = searchField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // "(?i)" makes the regex case-insensitive
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, searchColumnIndex));
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) { filter(); }
    @Override
    public void removeUpdate(DocumentEvent e) { filter(); }
    @Override
    public void changedUpdate(DocumentEvent e) { filter(); }
}