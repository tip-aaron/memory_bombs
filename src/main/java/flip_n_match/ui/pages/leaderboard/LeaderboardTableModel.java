package flip_n_match.ui.pages.leaderboard;

import flip_n_match.lib.Scorer;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Place", "Name", "Time", "Date"};
    private List<Scorer.ScoreEntry> scores = new ArrayList<>();

    public void setScores(List<Scorer.ScoreEntry> scores) {
        this.scores = scores;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return scores.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // --- ADD THIS METHOD ---
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class; // Forces numerical sorting for "Place"
            case 1, 2, 3 -> String.class; // Standard alphabetical sorting for the rest
            default -> Object.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Scorer.ScoreEntry entry = scores.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex + 1; // Returns an Integer, which is now properly recognized
            case 1 -> entry.name();
            case 2 -> entry.getFormattedTime();
            case 3 -> entry.getFormattedDateTimestamp();
            default -> null;
        };
    }
}