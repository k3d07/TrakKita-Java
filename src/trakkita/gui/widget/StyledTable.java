package trakkita.gui.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/** Table with an ink header, zebra rows and a marigold selection. */
public class StyledTable extends JTable {
    public StyledTable(TableModel model) {
        super(model);
        setFont(Theme.mono(13, false));
        setForeground(Theme.INK);
        setRowHeight(32);
        setShowVerticalLines(false);
        setGridColor(Theme.DOT);
        setIntercellSpacing(new Dimension(0, 1));
        setSelectionBackground(Theme.MARIGOLD);
        setSelectionForeground(Theme.INK);
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setPreferredSize(new Dimension(0, 36));
        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, false, false, r, c);
                setBackground(Theme.INK);
                setForeground(Theme.CREAM);
                setFont(Theme.label(15));
                setText(v == null ? "" : v.toString().toUpperCase());
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component c = super.prepareRenderer(renderer, row, col);
        if (!isRowSelected(row)) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.ZEBRA);
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }
        return c;
    }
}
