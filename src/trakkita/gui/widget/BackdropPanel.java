package trakkita.gui.widget;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/** A panel painted with the cream dotted background. Screens sit on top of it. */
public class BackdropPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(Theme.dotsPaint());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
