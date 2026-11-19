package trakkita.gui.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import trakkita.util.Money;

/** Taxi-meter style readout: dark panel, glowing digits, optional count-up. */
public class LedDisplay extends JComponent {
    private final String caption;
    private final float valueSize;
    private final CountUp counter = new CountUp();
    private String text = "-";
    private boolean money;
    private boolean signed;
    private Color color = Theme.MARIGOLD;

    public LedDisplay(String caption, float valueSize) {
        this.caption = caption;
        this.valueSize = valueSize;
        setOpaque(false);
        Anim.bind(this, () -> {
            if (money && counter.step()) {
                text = format(counter.get());
            }
            return true;
        });
    }

    private String format(double v) {
        return (signed && v > 0 ? "+" : "") + Money.peso(v);
    }

    public void setColor(Color c) {
        this.color = c;
        repaint();
    }

    /** Shows a peso amount that counts up. If signed, positive values get a leading plus. */
    public void setAmount(double value, boolean signed) {
        this.money = true;
        this.signed = signed;
        counter.set(value, true);
        if (Anim.isInstant() || !isDisplayable()) {
            counter.set(value, false);
        }
        text = format(counter.get());
        repaint();
    }

    public void setText(String t) {
        this.money = false;
        this.text = t;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics v = getFontMetrics(Theme.mono(valueSize, true));
        FontMetrics c = getFontMetrics(Theme.label(13));
        return new Dimension(Math.max(150, v.stringWidth("+\u20B11,000.00") + 36), c.getHeight() + v.getHeight() + 26);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        double w = getWidth() - CardPanel.SHADOW;
        double h = getHeight() - CardPanel.SHADOW;
        Theme.hardBox(g, 0, 0, w, h, 14, CardPanel.SHADOW, Theme.LED_BG, Theme.INK, 2f);
        g.setFont(Theme.label(13));
        FontMetrics cf = g.getFontMetrics();
        g.setColor(Theme.AQUA);
        g.drawString(caption.toUpperCase(), 14, 10 + cf.getAscent());
        java.awt.Font vf = Theme.mono(valueSize, true);
        g.setFont(vf);
        FontMetrics vm = g.getFontMetrics();
        double glow = 0.55 + 0.45 * Math.sin(Anim.loop(3200) * Math.PI * 2);
        Theme.glowText(g, text, vf, 14f, (float) (h - 12 - vm.getDescent()), color, glow);
        g.dispose();
    }
}
