package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** White card with a hard shadow. Optional colored "ticket" header strip with a caption. */
public class CardPanel extends JPanel {
    public static final int SHADOW = 5;
    public static final int HEAD = 28;

    private final String caption;
    private final Color headBg;
    private final Color headFg;
    private Color bg = Color.WHITE;
    private boolean dashed;

    public CardPanel() {
        this(null, null, null);
    }

    public CardPanel(String caption, Color headBg, Color headFg) {
        this.caption = caption;
        this.headBg = headBg;
        this.headFg = headFg;
        setOpaque(false);
        int top = caption == null ? 14 : HEAD + 12;
        setBorder(BorderFactory.createEmptyBorder(top, 16, 14 + SHADOW, 16 + SHADOW));
    }

    public CardPanel background(Color c) {
        this.bg = c;
        return this;
    }

    public CardPanel dashed(boolean d) {
        this.dashed = d;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        double w = getWidth() - SHADOW;
        double h = getHeight() - SHADOW;
        RoundRectangle2D body = new RoundRectangle2D.Double(0, 0, w, h, 16, 16);
        g.setColor(Theme.INK);
        g.fill(new RoundRectangle2D.Double(SHADOW, SHADOW, w, h, 16, 16));
        g.setColor(bg);
        g.fill(body);
        if (caption != null) {
            Graphics2D c = (Graphics2D) g.create();
            c.setClip(body);
            c.setColor(headBg);
            c.fillRect(0, 0, (int) w, HEAD);
            c.setColor(Theme.INK);
            c.fillRect(0, HEAD - 1, (int) w, 2);
            c.setFont(Theme.label(14));
            FontMetrics fm = c.getFontMetrics();
            c.setColor(headFg);
            c.drawString(caption.toUpperCase(), 14, (HEAD - fm.getHeight()) / 2 + fm.getAscent());
            c.dispose();
        }
        g.setColor(Theme.INK);
        g.setStroke(dashed
                ? new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {7f, 4f}, 0f)
                : new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, 15, 15));
        g.dispose();
    }
}
