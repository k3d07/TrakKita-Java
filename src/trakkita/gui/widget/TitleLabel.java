package trakkita.gui.widget;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** Screen title in the display font with a marigold highlighter stripe behind it. */
public class TitleLabel extends JComponent {
    private final String text;

    public TitleLabel(String text) {
        this.text = text;
        setFont(Theme.display(30));
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(text) + 12, fm.getHeight() + 8);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        int baseline = 4 + fm.getAscent();
        g.setColor(Theme.MARIGOLD);
        g.fillRect(0, baseline - fm.getAscent() / 3, fm.stringWidth(text) + 8, fm.getAscent() / 2 + 2);
        g.setColor(Theme.INK);
        g.drawString(text, 0, baseline);
        g.dispose();
    }
}
