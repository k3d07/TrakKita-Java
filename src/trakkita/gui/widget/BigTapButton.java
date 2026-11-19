package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import trakkita.util.Money;

/** Huge fare button. Presses down into its shadow and releases a floating "+PHP" on each tap. */
public class BigTapButton extends JButton {
    private final String caption;
    private final String amountText;
    private final double amount;
    private final String keyHint;
    private final Color fill;
    private final Color fg;
    private double press;
    private boolean hover;
    private final List<Long> floats = new ArrayList<>();

    public BigTapButton(String caption, double amount, String keyHint, Color fill, Color fg) {
        this.caption = caption;
        this.amount = amount;
        this.amountText = Money.peso(amount);
        this.keyHint = keyHint;
        this.fill = fill;
        this.fg = fg;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; }
            @Override public void mouseExited(MouseEvent e) { hover = false; }
        });
        addActionListener(e -> {
            floats.add(Anim.now());
            if (floats.size() > 8) {
                floats.remove(0);
            }
        });
        Anim.bind(this, () -> {
            double target = getModel().isPressed() ? 1 : 0;
            double n = press + (target - press) * 0.45;
            boolean changed = Math.abs(n - press) > 0.002;
            press = n;
            floats.removeIf(t -> Anim.now() - t > 800);
            return changed || !floats.isEmpty();
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        double w = getWidth() - 10;
        double h = getHeight() - 10;
        g.setColor(Theme.INK);
        g.fill(new RoundRectangle2D.Double(8, 8, w, h, 26, 26));
        double b = 2 + press * 6 - (hover ? 2 : 0);
        g.setColor(fill);
        g.fill(new RoundRectangle2D.Double(b, b, w, h, 26, 26));
        g.setColor(Theme.INK);
        g.setStroke(new BasicStroke(3f));
        g.draw(new RoundRectangle2D.Double(b + 1.5, b + 1.5, w - 3, h - 3, 25, 25));

        g.setColor(fg);
        g.setFont(Theme.label(24));
        FontMetrics fm = g.getFontMetrics();
        double cx = b + w / 2;
        double cy = b + h / 2;
        g.drawString(caption.toUpperCase(), (float) (cx - fm.stringWidth(caption.toUpperCase()) / 2.0), (float) (cy - 46));
        g.setFont(Theme.mono(66, true));
        fm = g.getFontMetrics();
        g.drawString(amountText, (float) (cx - fm.stringWidth(amountText) / 2.0), (float) (cy + 20));
        g.setFont(Theme.mono(15, true));
        fm = g.getFontMetrics();
        int kw = fm.stringWidth(keyHint) + 20;
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(cx - kw / 2.0, cy + 42, kw, 26, 8, 8));
        g.drawString(keyHint, (float) (cx - fm.stringWidth(keyHint) / 2.0), (float) (cy + 61));

        g.setFont(Theme.mono(26, true));
        String plus = "+" + Money.peso(amount);
        fm = g.getFontMetrics();
        for (long t : floats) {
            double p = Theme.clamp((Anim.now() - t) / 800.0);
            Theme.setAlpha(g, (float) (1 - p));
            g.setColor(Theme.INK);
            g.drawString(plus, (float) (cx - fm.stringWidth(plus) / 2.0), (float) (cy - 70 - p * 70));
        }
        g.dispose();
    }
}
