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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
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
    private final DefaultTableModel historyModel = UI.model("Date", "Discounted", "Regular", "Total");
    private final JTable historyTable = UI.table(historyModel);
    private List<TapSession> shownSessions = new ArrayList<>();
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
        JPanel history = new JPanel(new BorderLayout(8, 6));
        history.setOpaque(false);
        history.add(UI.muted("Saved sessions"), BorderLayout.NORTH);
        history.add(UI.tableCard(historyTable), BorderLayout.CENTER);
        ThemedButton deleteSession = UI.button("Delete selected session", Theme.DARK_RED, Color.WHITE);
        deleteSession.setEnabled(false);
        JPanel deleteBox = new JPanel(new BorderLayout());
        deleteBox.setOpaque(false);
        deleteBox.add(deleteSession, BorderLayout.NORTH);
        history.add(deleteBox, BorderLayout.EAST);
        history.setPreferredSize(new java.awt.Dimension(200, 190));
        JPanel main = new JPanel(new BorderLayout(0, 10)) {
            @Override public void doLayout() {
                int historyHeight = getHeight() < 360 ? 168 : 190;
                if (history.getPreferredSize().height != historyHeight) {
                    history.setPreferredSize(new java.awt.Dimension(200, historyHeight));
                }
                super.doLayout();
            }
        };
        main.setOpaque(false);
        main.add(pad, BorderLayout.CENTER);
        main.add(history, BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);

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
        deleteSession.addActionListener(e -> deleteSession());
        historyTable.getSelectionModel().addListSelectionListener(e ->
                deleteSession.setEnabled(historyTable.getSelectedRow() >= 0));
        // keyboard taps click the on-screen button so it animates too
        bindKey(KeyEvent.VK_1, "tap-discounted", () -> discounted.doClick(90));
        bindKey(KeyEvent.VK_2, "tap-regular", () -> regular.doClick(90));
        update();
        TableStyle.padLeftColumn(historyTable, 0);
        TableStyle.rightAlignColumns(historyTable, 1, 2, 3);
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
        double sessionTotal = current.getTotalCollected();
        String[] options = {"Save and add to Profit / Loss", "Save only", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "Save this session (" + Money.peso(sessionTotal) + ")?", "End Quick-Tap session",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice != 0 && choice != 1) return;

        LocalDate sessionDate = current.getDate();
        ctx.sessions().add(current);
        persist();
        current = new TapSession(LocalDate.now());
        update();
        refresh();
        if (choice == 0) {
            ctx.setPendingEntry(sessionDate, sessionTotal);
            goTo("Profit / Loss");
        }
    }

    @Override
    public void refresh() {
        update();
        shownSessions = ctx.sessions().getAll();
        Collections.reverse(shownSessions);
        historyModel.setRowCount(0);
        for (TapSession session : shownSessions) {
            historyModel.addRow(new Object[] {session.getDate(), session.getDiscountedTaps(),
                    session.getRegularTaps(), Money.peso(session.getTotalCollected())});
        }
    }

    private void deleteSession() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            UI.info(this, "Select a saved session first.");
            return;
        }
        int modelRow = historyTable.convertRowIndexToModel(row);
        TapSession session = shownSessions.get(modelRow);
        if (UI.confirm(this, "Delete the saved session from " + session.getDate() + "?")) {
            ctx.sessions().remove(session);
            persist();
            refresh();
        }
    }
}
