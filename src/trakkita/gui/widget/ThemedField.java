package trakkita.gui.widget;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/** Rounded text field with an ink outline and a marigold ring while focused. */
public class ThemedField extends JTextField {
    public ThemedField(int columns) {
        super(columns);
        setOpaque(false);
        setFont(Theme.mono(14, false));
        setForeground(Theme.INK);
        setCaretColor(Theme.INK);
        setSelectionColor(Theme.MARIGOLD);
        setSelectedTextColor(Theme.INK);
        setBorder(BorderFactory.createEmptyBorder(9, 11, 9, 11));
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { repaint(); }
            @Override public void focusLost(FocusEvent e) { repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        g.setColor(Theme.PAPER);
        g.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
        g.dispose();
        super.paintComponent(g0);
    }

    @Override
    protected void paintBorder(Graphics g0) {
        Graphics2D g = Theme.aa(g0);
        if (hasFocus()) {
            g.setColor(Theme.MARIGOLD);
            g.setStroke(new BasicStroke(4f));
            g.draw(new RoundRectangle2D.Double(2, 2, getWidth() - 4, getHeight() - 4, 12, 12));
        }
        g.setColor(Theme.INK);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
        g.dispose();
    }
}
