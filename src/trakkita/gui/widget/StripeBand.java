package trakkita.gui.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

/** Red / yellow / teal diagonal stripes, like the side panel of a jeepney. */
public class StripeBand extends JComponent {
    private static final Color[] COLORS = {Theme.RED, Theme.MARIGOLD, Theme.TEAL};

    public StripeBand() {
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 14);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 14);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        int w = getWidth();
        int h = getHeight();
        g.setClip(new RoundRectangle2D.Double(0, 0, w, h, h, h));
        int i = 0;
        for (int x = -h; x < w + h; x += 10) {
            Path2D p = new Path2D.Double();
            p.moveTo(x + h, 0);
            p.lineTo(x + h + 10, 0);
            p.lineTo(x + 10, h);
            p.lineTo(x, h);
            p.closePath();
            g.setColor(COLORS[i++ % COLORS.length]);
            g.fill(p);
        }
        g.dispose();
    }
}
