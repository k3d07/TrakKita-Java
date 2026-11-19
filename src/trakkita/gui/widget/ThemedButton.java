package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

/** Chunky button: lifts on hover and pushes into its shadow when pressed (animated). */
public class ThemedButton extends JButton {
    private final Color fill;
    private final Color fg;
    private double lift;
    private double press;
    private boolean hover;

    public ThemedButton(String text, Color fill, Color fg) {
        super(text);
        this.fill = fill;
        this.fg = fg;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Theme.body(15));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; }
            @Override public void mouseExited(MouseEvent e) { hover = false; }
        });
        Anim.bind(this, this::step);
    }

    private boolean step() {
        double targetLift = hover && isEnabled() ? 1 : 0;
        double targetPress = getModel().isPressed() ? 1 : 0;
        double nl = lift + (targetLift - lift) * 0.35;
        double np = press + (targetPress - press) * 0.5;
        boolean changed = Math.abs(nl - lift) > 0.002 || Math.abs(np - press) > 0.002;
        lift = nl;
        press = np;
        return changed;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(getText()) + 44, fm.getHeight() + 28);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        double w = getWidth() - 6;
        double h = getHeight() - 6;
        if (isEnabled()) {
            g.setColor(Theme.INK);
            g.fill(new RoundRectangle2D.Double(4, 4, w, h, 12, 12));
        }
        double b = 2 - lift + press * 2;
        g.setColor(isEnabled() ? fill : Color.LIGHT_GRAY);
        g.fill(new RoundRectangle2D.Double(b, b, w, h, 12, 12));
        g.setColor(Theme.INK);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(b + 1, b + 1, w - 2, h - 2, 11, 11));
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        g.setColor(fg);
        g.drawString(getText(), (int) (b + (w - fm.stringWidth(getText())) / 2.0),
                (int) (b + (h - fm.getHeight()) / 2.0 + fm.getAscent()));
        g.dispose();
    }
}
