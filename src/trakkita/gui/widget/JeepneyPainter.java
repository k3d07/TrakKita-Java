package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/** Draws the little jeepney mascot with Java2D shapes. The drawing is 120 x 56 units. */
public final class JeepneyPainter {
    public static final double WIDTH = 120;
    public static final double HEIGHT = 56;

    private JeepneyPainter() { }

    public static void paint(Graphics2D g0, double x, double y, double scale) {
        Graphics2D g = (Graphics2D) g0.create();
        g.translate(x, y);
        g.scale(scale, scale);
        g.setStroke(new BasicStroke(2f));
        g.setColor(Theme.MARIGOLD);
        g.fill(new RoundRectangle2D.Double(14, 5, 80, 11, 8, 8));
        g.setColor(Theme.RED);
        g.fill(new RoundRectangle2D.Double(6, 14, 104, 27, 14, 14));
        g.setColor(Theme.INK);
        g.draw(new RoundRectangle2D.Double(6, 14, 104, 27, 14, 14));
        g.setColor(Theme.CREAM);
        for (int wx : new int[] {18, 43, 68}) {
            g.fill(new RoundRectangle2D.Double(wx, 18, 20, 10, 4, 4));
        }
        g.setColor(Theme.MARIGOLD);
        g.fill(new Rectangle2D.Double(8, 31, 100, 4));
        g.setColor(Theme.RED);
        g.fill(new Rectangle2D.Double(110, 22, 7, 19));
        g.setColor(Theme.INK);
        g.draw(new Rectangle2D.Double(110, 22, 7, 19));
        for (int cx : new int[] {30, 88}) {
            g.setColor(Theme.INK);
            g.fill(new Ellipse2D.Double(cx - 8, 35, 16, 16));
            g.setColor(Theme.CREAM);
            g.fill(new Ellipse2D.Double(cx - 3, 40, 6, 6));
        }
        g.dispose();
    }
}
