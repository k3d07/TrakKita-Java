package trakkita.gui.widget;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

/** Revenue-to-cost gauge from 0 to 120% with a marker at 100% (the break-even line). */
public class CoverageGauge extends JComponent {
    private double pct;
    private boolean loss;
    private long start = Anim.now();

    public CoverageGauge() {
        setOpaque(false);
        Anim.bind(this, () -> Anim.progress(start, 1100) < 1.0);
    }

    public void setCoverage(double pct, boolean loss) {
        this.pct = pct;
        this.loss = loss;
        start = Anim.now();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 58);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        int w = getWidth() - 4;
        g.setFont(Theme.label(14));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Theme.INK);
        g.drawString(String.format("COVERAGE %.1f%%", pct), 0, fm.getAscent());
        String right = "BREAK-EVEN LINE";
        g.setColor(Theme.MUTED);
        g.drawString(right, w - fm.stringWidth(right), fm.getAscent());

        int y = 26;
        int h = 18;
        RoundRectangle2D track = new RoundRectangle2D.Double(0, y, w, h, h, h);
        g.setColor(Theme.DOT);
        g.fill(track);
        double shown = pct * Theme.easeOut(Anim.progress(start, 1100));
        double fillW = Math.min(w, shown / 120.0 * w);
        if (fillW > 2) {
            RoundRectangle2D fill = new RoundRectangle2D.Double(0, y, fillW, h, h, h);
            if (loss) {
                Theme.hatch(g, fill, Theme.RED, java.awt.Color.WHITE, 6);
            } else {
                g.setColor(Theme.TEAL);
                g.fill(fill);
            }
        }
        g.setColor(Theme.INK);
        g.setStroke(new java.awt.BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(1, y, w - 1, h, h, h));
        int mx = (int) (w * 100.0 / 120.0);
        g.setStroke(new java.awt.BasicStroke(3f));
        g.drawLine(mx, y - 5, mx, y + h + 5);
        g.dispose();
    }
}
