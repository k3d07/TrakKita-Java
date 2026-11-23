package trakkita.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

/**
 * Aligns numeric table columns (cells AND headers) to the right with equal
 * padding.
 */
public final class TableStyle {

    private static final int PAD_LEFT = 8;
    private static final int PAD_RIGHT = 14;

    private TableStyle() {
    }

    /** Sets preferred widths by view-column order. */
    public static void setColumnWidths(JTable table, int... widths) {
        for (int i = 0; i < widths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    /**
     * Right-aligns the given view columns. Call AFTER you set your own renderers
     * (for example the green/red Net Profit renderer), so this wraps them.
     */
    public static void rightAlignColumns(JTable table, int... columns) {
        Set<Integer> numeric = new HashSet<>();
        for (int c : columns) {
            numeric.add(c);
        }

        for (int c : columns) {
            TableColumn col = table.getColumnModel().getColumn(c);
            final TableCellRenderer inner = col.getCellRenderer(); // may be null
            col.setCellRenderer((t, value, selected, focus, row, column) -> {
                TableCellRenderer base = (inner != null)
                        ? inner
                        : t.getDefaultRenderer(t.getColumnClass(column));
                Component comp = base.getTableCellRendererComponent(
                        t, value, selected, focus, row, column);
                if (comp instanceof JLabel lbl) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, PAD_LEFT, 0, PAD_RIGHT));
                }
                return comp;
            });
        }

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        final TableCellRenderer originalHeader = header.getDefaultRenderer();
        header.setDefaultRenderer((t, value, selected, focus, row, column) -> {
            Component comp = originalHeader.getTableCellRendererComponent(
                    t, value, selected, focus, row, column);
            if (comp instanceof JLabel lbl) {
                boolean right = numeric.contains(column);
                lbl.setHorizontalAlignment(right ? SwingConstants.RIGHT : SwingConstants.LEFT);
                lbl.setBorder(BorderFactory.createEmptyBorder(
                        0, right ? PAD_LEFT : 8, 0, right ? PAD_RIGHT : 8));
            }
            return comp;
        });
        header.repaint();
    }

    /**
     * Left padding for text columns so the Date column lines up with its header.
     */
    public static void padLeftColumn(JTable table, int column) {
        TableColumn col = table.getColumnModel().getColumn(column);
        final TableCellRenderer inner = col.getCellRenderer();
        col.setCellRenderer((t, value, selected, focus, row, c) -> {
            TableCellRenderer base = (inner != null)
                    ? inner
                    : t.getDefaultRenderer(t.getColumnClass(c));
            Component comp = base.getTableCellRendererComponent(t, value, selected, focus, row, c);
            if (comp instanceof JLabel lbl) {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            }
            return comp;
        });
    }
}
