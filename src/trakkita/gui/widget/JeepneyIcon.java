package trakkita.gui.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** The jeepney mascot, gently bobbing up and down. */
public class JeepneyIcon extends JComponent {
    public JeepneyIcon() {
        setOpaque(false);
        Anim.bind(this, () -> true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(120, 62);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        double scale = Math.min(getWidth() / JeepneyPainter.WIDTH, (getHeight() - 6) / JeepneyPainter.HEIGHT);
        double bob = -4 * Math.sin(Math.PI * Anim.loop(1600));
        JeepneyPainter.paint(g, 2, 5 + bob, scale);
        g.dispose();
    }
}
