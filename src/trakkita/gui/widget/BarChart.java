package trakkita.gui.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import trakkita.model.DailyEarning;
import trakkita.util.Money;

/** Net-income bar chart. Bars grow up one after another; losses are red with stripes. */
public class BarChart extends JComponent {
    private List<DailyEarning> data = new ArrayList<>();
    private long start = Anim.now();

    public BarChart() {
        setOpaque(false);
        Anim.bind(this, () -> Anim.progress(start, 1600) < 1.0);
    }

    public void setData(List<DailyEarning> data) {
        this.data = data;
        replay();
    }

    /** Restarts the grow-in animation. */
    public void replay() {
        start = Anim.now();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        int w = getWidth();
        int h = getHeight();
        if (data.isEmpty()) {
            g.setFont(Theme.body(15));
            g.setColor(Theme.MUTED);
            g.drawString("No entries yet. Add a daily entry to see your chart.", 4, 28);
            g.dispose();
            return;
        }
        int top = 24;
        double max = 0;
        double min = 0;
        for (DailyEarning e : data) {
            max = Math.max(max, e.getNetProfit());
            min = Math.min(min, e.getNetProfit());
        }
        int bottom = min < 0 ? 48 : 26;   // extra room for the label under a loss bar
        double range = (max - min) == 0 ? 1 : (max - min);
        int plotH = h - top - bottom;
        int zeroY = top + (int) (plotH * (max / range));
        int slot = w / data.size();
        int bw = Math.min(64, slot - 16);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
        for (int i = 0; i < data.size(); i++) {
            DailyEarning e = data.get(i);
            double n = e.getNetProfit();
            double p = Theme.easeOut(Theme.clamp(
                    (Anim.progress(start, 1600) * 1600 - i * 90) / 700.0));
            int barH = (int) Math.max(3, Math.abs(n / range * plotH) * p);
            int x = i * slot + (slot - bw) / 2;
            int y = n >= 0 ? zeroY - barH : zeroY;
            RoundRectangle2D bar = new RoundRectangle2D.Double(x, y, bw, barH, 10, 10);
            if (n < 0) {
                Theme.hatch(g, bar, Theme.RED, Color.WHITE, 6);
            } else {
                g.setColor(i == data.size() - 1 ? Theme.MARIGOLD : Theme.INDIGO);
                g.fill(bar);
            }
            g.setColor(Theme.INK);
            g.setStroke(new java.awt.BasicStroke(2f));
            g.draw(bar);

            g.setFont(Theme.mono(12, true));
            FontMetrics fm = g.getFontMetrics();
            String v = Money.pesoShort(n);
            g.setColor(n < 0 ? Theme.DARK_RED : Theme.INK);
            Theme.setAlpha(g, (float) p);
            g.drawString(v, x + (bw - fm.stringWidth(v)) / 2, n >= 0 ? y - 6 : y + barH + 15);
            Theme.setAlpha(g, 1f);

            g.setFont(Theme.label(13));
            fm = g.getFontMetrics();
            String d = e.getDate().format(fmt).toUpperCase();
            g.setColor(Theme.MUTED);
            g.drawString(d, x + (bw - fm.stringWidth(d)) / 2, h - 6);
        }
        g.setColor(Theme.INK);
        g.setStroke(new java.awt.BasicStroke(2f));
        g.drawLine(0, zeroY, w, zeroY);
        g.dispose();
    }
}
