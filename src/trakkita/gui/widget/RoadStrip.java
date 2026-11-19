package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** A dashed road with a jeepney driving across it. */
public class RoadStrip extends JComponent {
    public RoadStrip() {
        setOpaque(false);
        Anim.bind(this, () -> true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 48);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Theme.INK);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {12f, 9f}, 0f));
        g.drawLine(0, h - 4, w, h - 4);
        double scale = 0.62;
        double x = Anim.isInstant() ? w * 0.62 : ((Anim.now() * 0.11) % (w + 240)) - 130;
        JeepneyPainter.paint(g, x, h - 4 - JeepneyPainter.HEIGHT * scale + 2, scale);
        g.dispose();
    }
}
