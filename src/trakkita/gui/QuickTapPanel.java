package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import trakkita.gui.widget.BigTapButton;
import trakkita.gui.widget.LedDisplay;
import trakkita.gui.widget.RoadStrip;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.model.TapSession;
import trakkita.service.AppContext;
import trakkita.util.Money;

public class QuickTapPanel extends ScreenPanel {
    private TapSession current = new TapSession(LocalDate.now());
    private final LedDisplay total = new LedDisplay("Running total", 40);
    private final LedDisplay count = new LedDisplay("Passengers", 28);
    private final LedDisplay split = new LedDisplay("Disc. / Reg.", 28);

    public QuickTapPanel(AppContext ctx) {
        super(ctx);
        add(header("Quick-Tap", "One tap per passenger. Press 1 or 2 on your keyboard to tap faster."), BorderLayout.NORTH);

        BigTapButton discounted = new BigTapButton("Discounted", TapSession.DISCOUNTED_FARE, "1", Theme.TEAL, Color.WHITE);
        BigTapButton regular = new BigTapButton("Regular", TapSession.REGULAR_FARE, "2", Theme.MARIGOLD, Theme.INK);
        JPanel pad = new JPanel(new GridLayout(1, 2, 18, 0));
        pad.setOpaque(false);
        pad.add(discounted);
        pad.add(regular);
        add(pad, BorderLayout.CENTER);

        total.setColor(Theme.MINT);
        count.setColor(Theme.SIDE_TEXT);
        split.setColor(Theme.SIDE_TEXT);
        JPanel meters = new JPanel(new GridBagLayout());
        meters.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.insets = new Insets(0, 0, 0, 12);
        gc.weightx = 2;
        meters.add(total, gc);
        gc.weightx = 1;
        meters.add(count, gc);
        meters.add(split, gc);

        ThemedButton undo = UI.button("Undo last tap", false);
        ThemedButton end = UI.button("End and save session", true);
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        UI.stack(buttons, 10, undo, end);
        UI.fullWidth(undo);
        UI.fullWidth(end);

        JPanel bar = new JPanel(new BorderLayout(14, 0));
        bar.setOpaque(false);
        bar.add(meters, BorderLayout.CENTER);
        bar.add(buttons, BorderLayout.EAST);

        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.setOpaque(false);
        south.add(bar, BorderLayout.CENTER);
        south.add(new RoadStrip(), BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        discounted.addActionListener(e -> tap(true));
        regular.addActionListener(e -> tap(false));
        undo.addActionListener(e -> {
            if (!current.undo()) {
                UI.info(this, "Nothing to undo.");
            }
            update();
        });
        end.addActionListener(e -> endSession());
        // keyboard taps click the on-screen button so it animates too
        bindKey(KeyEvent.VK_1, "tap-discounted", () -> discounted.doClick(90));
        bindKey(KeyEvent.VK_2, "tap-regular", () -> regular.doClick(90));
        update();
    }

    private void bindKey(int keyCode, String name, Runnable action) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void tap(boolean discounted) {
        current.tap(discounted);
        update();
    }

    private void update() {
        total.setAmount(current.getTotalCollected(), false);
        count.setText(String.valueOf(current.getTapCount()));
        split.setText(current.getDiscountedTaps() + " / " + current.getRegularTaps());
    }

    private void endSession() {
        if (current.getTapCount() == 0) {
            UI.info(this, "There are no taps to save yet.");
            return;
        }
        if (UI.confirm(this, "Save this session (" + Money.peso(current.getTotalCollected()) + ") and start a new one?")) {
            ctx.sessions().add(current);
            persist();
            current = new TapSession(LocalDate.now());
            update();
        }
    }

    @Override
    public void refresh() {
        update();
    }
}
