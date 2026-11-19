package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** Status banner. In "bad" mode (a loss) its outline pulses and it shows a warning triangle. */
public class Banner extends JPanel {
    private final JTextArea text = new JTextArea();
    private boolean bad = true;

    public Banner() {
        setOpaque(false);
        setLayout(new BorderLayout());
        text.setEditable(false);
        text.setFocusable(false);
        text.setOpaque(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setFont(Theme.mono(13, true));
        add(text, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 46, 10, 12));
        Anim.bind(this, () -> bad);
    }

    public void setMessage(String message, boolean bad) {
        this.bad = bad;
        text.setText(message);
        text.setForeground(bad ? new Color(0x8A1C12) : new Color(0x0B5B57));
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(58, d.height));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        int w = getWidth() - 4;
        int h = getHeight() - 4;
        Color line = bad ? Theme.DARK_RED : Theme.TEAL;
        if (bad) {
            double pulse = 0.5 + 0.5 * Math.sin(Anim.loop(2000) * Math.PI * 2);
            g.setColor(Theme.alpha(Theme.DARK_RED, (int) (70 * pulse)));
            g.setStroke(new BasicStroke(6f));
            g.draw(new RoundRectangle2D.Double(3, 3, w - 2, h - 2, 14, 14));
        }
        g.setColor(bad ? Theme.PALE_RED : Theme.PALE_GREEN);
        g.fill(new RoundRectangle2D.Double(2, 2, w, h, 12, 12));
        g.setColor(line);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(2, 2, w, h, 12, 12));
        int cy = getHeight() / 2 - 2;
        if (bad) {
            Path2D tri = new Path2D.Double();
            tri.moveTo(24, cy - 11);
            tri.lineTo(36, cy + 10);
            tri.lineTo(12, cy + 10);
            tri.closePath();
            g.setColor(Theme.DARK_RED);
            g.fill(tri);
            g.setColor(Color.WHITE);
            g.fillRect(23, cy - 4, 3, 8);
            g.fillRect(23, cy + 5, 3, 3);
        } else {
            g.setColor(Theme.TEAL);
            g.fillOval(12, cy - 11, 22, 22);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(18, cy, 22, cy + 5);
            g.drawLine(22, cy + 5, 29, cy - 4);
        }
        g.dispose();
    }
}
