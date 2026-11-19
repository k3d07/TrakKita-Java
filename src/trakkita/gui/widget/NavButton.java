package trakkita.gui.widget;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

/** Sidebar navigation button: slides right on hover, marigold pill when active. */
public class NavButton extends JButton {
    private boolean active;
    private boolean hover;
    private double lift;

    public NavButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Theme.label(18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; }
            @Override public void mouseExited(MouseEvent e) { hover = false; }
        });
        Anim.bind(this, () -> {
            double n = lift + ((hover ? 1 : 0) - lift) * 0.3;
            boolean changed = Math.abs(n - lift) > 0.002;
            lift = n;
            return changed;
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(180, 46);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 46);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        double w = getWidth() - 6;
        double h = getHeight() - 6;
        if (active) {
            g.setColor(Theme.RED);
            g.fill(new RoundRectangle2D.Double(4, 4, w, h, 12, 12));
            g.setColor(Theme.MARIGOLD);
            g.fill(new RoundRectangle2D.Double(0, 0, w, h, 12, 12));
        } else if (lift > 0.01) {
            g.setColor(Theme.alpha(java.awt.Color.WHITE, (int) (30 * lift)));
            g.fill(new RoundRectangle2D.Double(0, 0, w, h, 12, 12));
        }
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        g.setColor(active ? Theme.INK : Theme.SIDE_TEXT);
        g.drawString(getText().toUpperCase(), (float) (16 + (active ? 0 : lift * 6)),
                (float) ((h - fm.getHeight()) / 2.0 + fm.getAscent()));
        g.dispose();
    }
}
