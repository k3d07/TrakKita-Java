package trakkita.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import trakkita.gui.widget.Anim;
import trakkita.gui.widget.Theme;
import trakkita.service.AppContext;

/** Parent of every screen. AppShell calls refresh() and onShown() on each one (polymorphism). */
public abstract class ScreenPanel extends JPanel {
    private static final long ENTRANCE_MS = 450;

    protected final AppContext ctx;
    protected final StatusLine statusLine = new StatusLine();
    private Consumer<String> navigator = name -> { };
    private long enteredAt = -100000;

    protected ScreenPanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));
        Anim.bind(this, () -> Anim.progress(enteredAt, ENTRANCE_MS) < 1.0);
    }

    /** Reload everything this screen shows from the AppContext. */
    public abstract void refresh();

    /** Called each time the screen becomes visible. Subclasses may add more animations. */
    public void onShown() {
        enteredAt = Anim.now();
        repaint();
    }

    public void setNavigator(Consumer<String> navigator) {
        this.navigator = navigator;
    }

    protected void goTo(String screenName) {
        navigator.accept(screenName);
    }

    protected void persist() {
        try {
            ctx.save();
            statusLine.showMessage("Saved to data/puv_records.csv.");
        } catch (IOException e) {
            UI.error(this, "Your change is on screen but could not be saved to the file: " + e.getMessage());
            statusLine.showMessage("Could not save the change.", true);
        }
    }

    protected JPanel header(String titleText, String subtitleText) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(UI.title(titleText), BorderLayout.NORTH);
        p.add(UI.sub(subtitleText), BorderLayout.CENTER);
        p.add(statusLine, BorderLayout.SOUTH);
        return p;
    }

    /** Fade and slide in when the screen appears. */
    @Override
    public void paint(Graphics g) {
        double t = Anim.progress(enteredAt, ENTRANCE_MS);
        if (t >= 1.0) {
            super.paint(g);
            return;
        }
        double e = Theme.easeOut(t);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) e));
        g2.translate(0, (1 - e) * 16);
        super.paint(g2);
        g2.dispose();
    }
}
