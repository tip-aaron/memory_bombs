package flip_n_match.ui.pages.leaderboard;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TopThreeBoldRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Convert view row to model row so filtering doesn't accidentally bold rank #4
        int modelRow = table.convertRowIndexToModel(row);

        if (modelRow < 3) {
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        } else {
            c.setFont(c.getFont().deriveFont(Font.PLAIN));
        }

        return c;
    }
}