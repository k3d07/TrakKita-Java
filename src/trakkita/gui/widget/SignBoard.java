package trakkita.gui.widget;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

/** Jeepney destination board: glowing name plus a scrolling marquee. */
public class SignBoard extends JComponent {
    private static final String TEXT = "PUV EARNINGS  /  FUEL LOG  /  BREAK-EVEN  /  QUICK-TAP  /  ";

    public SignBoard() {
        setOpaque(false);
        Anim.bind(this, () -> true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(192, 84);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 84);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Theme.LED_BG);
        g.fill(new RoundRectangle2D.Double(0, 0, w, h, 14, 14));
        g.setColor(Theme.MARIGOLD);
        g.setStroke(new java.awt.BasicStroke(3f));
        g.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 3, h - 3, 13, 13));

        java.awt.Font title = Theme.display(26);
        g.setFont(title);
        FontMetrics tm = g.getFontMetrics();
        double glow = 0.6 + 0.4 * Math.sin(Anim.loop(3000) * Math.PI * 2);
        Theme.glowText(g, "TRAKKITA", title, (w - tm.stringWidth("TRAKKITA")) / 2f, 14 + tm.getAscent(),
                Theme.MARIGOLD, glow);

        Shape oldClip = g.getClip();
        g.setClip(new RoundRectangle2D.Double(10, h - 28, w - 20, 22, 8, 8));
        g.setFont(Theme.label(13));
        FontMetrics fm = g.getFontMetrics();
        int cycle = fm.stringWidth(TEXT);
        double offset = (Anim.isInstant() ? 0 : (Anim.now() * 0.04)) % cycle;
        g.setColor(Theme.AQUA);
        int base = h - 12;
        g.drawString(TEXT, (float) (10 - offset), base);
        g.drawString(TEXT, (float) (10 - offset + cycle), base);
        g.setClip(oldClip);
        g.dispose();
    }
}
