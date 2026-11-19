package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** One receipt line: label ........ value. */
public class LeaderRow extends JComponent {
    private final String label;
    private String value = "-";
    private Color valueColor = Theme.INK;
    private final boolean bold;
    private final boolean topRule;

    public LeaderRow(String label, boolean bold, boolean topRule) {
        this.label = label;
        this.bold = bold;
        this.topRule = topRule;
        setOpaque(false);
    }

    public void setValue(String value, Color color) {
        this.value = value;
        this.valueColor = color;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, bold ? 36 : 30);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        int w = getWidth() - 2;
        g.setFont(Theme.mono(bold ? 15 : 14, bold));
        FontMetrics fm = g.getFontMetrics();
        int base = getHeight() - 8;
        if (topRule) {
            g.setColor(Theme.INK);
            g.setStroke(new BasicStroke(2f));
            g.drawLine(0, 2, w, 2);
        }
        g.setColor(Theme.INK);
        g.drawString(label, 0, base);
        g.setColor(valueColor);
        int vw = fm.stringWidth(value);
        g.drawString(value, w - vw, base);
        int x1 = fm.stringWidth(label) + 8;
        int x2 = w - vw - 8;
        if (x2 > x1) {
            g.setColor(Theme.INK);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[] {0.1f, 5f}, 0f));
            g.drawLine(x1, base - 3, x2, base - 3);
        }
        g.dispose();
    }
}
