package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import trakkita.gui.widget.BarChart;
import trakkita.gui.widget.CardPanel;
import trakkita.gui.widget.CountUpLabel;
import trakkita.gui.widget.JeepneyIcon;
import trakkita.gui.widget.LedDisplay;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.model.DailyEarning;
import trakkita.service.AppContext;

public class DashboardPanel extends ScreenPanel {
    private final CountUpLabel fares = new CountUpLabel(26);
    private final CountUpLabel fuel = new CountUpLabel(26);
    private final CountUpLabel boundary = new CountUpLabel(26);
    private final LedDisplay net = new LedDisplay("Net profit", 28);
    private final JLabel status = UI.sub(" ");
    private final JLabel bestRoute = new JLabel("-");
    private final BarChart chart = new BarChart();

    public DashboardPanel(AppContext ctx) {
        super(ctx);
        JPanel north = new JPanel(new BorderLayout(0, 14));
        north.setOpaque(false);
        north.add(header("Dashboard", "Your latest day and the last 7 entries at a glance."), BorderLayout.NORTH);
        JPanel kpis = new JPanel(new GridLayout(1, 4, 14, 0));
        kpis.setOpaque(false);
        kpis.add(kpi("Total fares", Theme.TEAL, Color.WHITE, fares));
        kpis.add(kpi("Fuel cost", Theme.MARIGOLD, Theme.INK, fuel));
        kpis.add(kpi("Boundary fee", Theme.DARK_RED, Color.WHITE, boundary));
        kpis.add(net);
        north.add(kpis, BorderLayout.CENTER);
        north.add(status, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        CardPanel chartCard = UI.ticket("Net income, last 7 entries", Theme.INDIGO, Theme.CREAM);
        chartCard.setLayout(new BorderLayout());
        chartCard.add(chart, BorderLayout.CENTER);
        add(chartCard, BorderLayout.CENTER);
        add(buildSideCard(), BorderLayout.EAST);
    }

    private CardPanel kpi(String caption, Color bg, Color fg, CountUpLabel value) {
        CardPanel p = UI.ticket(caption, bg, fg);
        p.setLayout(new BorderLayout());
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private CardPanel buildSideCard() {
        CardPanel p = UI.card().background(Theme.TEAL);
        p.setPreferredSize(new Dimension(270, 0));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel cap = new JLabel("BEST ROUTE TODAY");
        cap.setFont(Theme.label(15));
        cap.setForeground(Theme.AQUA);
        bestRoute.setFont(Theme.display(24));
        bestRoute.setForeground(Color.WHITE);
        JeepneyIcon jeep = new JeepneyIcon();
        jeep.setPreferredSize(new Dimension(130, 68));
        jeep.setMaximumSize(new Dimension(130, 68));
        JLabel note = new JLabel("Top efficiency score");
        note.setFont(Theme.body(14));
        note.setForeground(Color.WHITE);
        for (JComponent c : new JComponent[] {cap, bestRoute, jeep, note}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        p.add(cap);
        p.add(Box.createVerticalStrut(6));
        p.add(bestRoute);
        p.add(Box.createVerticalStrut(12));
        p.add(jeep);
        p.add(Box.createVerticalStrut(6));
        p.add(note);
        p.add(Box.createVerticalGlue());

        ThemedButton newEntry = UI.button("+ New daily entry", false);
        ThemedButton tap = UI.button("Open Quick-Tap", Theme.INK, Color.WHITE);
        ThemedButton export = UI.button("Export report", Theme.CREAM, Theme.INK);
        newEntry.addActionListener(e -> goTo("Profit / Loss"));
        tap.addActionListener(e -> goTo("Quick-Tap"));
        export.addActionListener(e -> exportReport());
        for (ThemedButton b : new ThemedButton[] {newEntry, tap, export}) {
            UI.fullWidth(b);
            p.add(b);
            p.add(Box.createVerticalStrut(8));
        }
        return p;
    }

    private void exportReport() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("trakkita_report.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(fc.getSelectedFile().toPath(), ctx.buildReport());
                UI.info(this, "Report saved.");
            } catch (IOException ex) {
                UI.error(this, "Could not save the report: " + ex.getMessage());
            }
        }
    }

    @Override
    public void refresh() {
        List<DailyEarning> all = ctx.earnings().getAll();
        if (all.isEmpty()) {
            fares.setDash();
            fuel.setDash();
            boundary.setDash();
            net.setText("-");
            net.setColor(Theme.MARIGOLD);
            status.setText("No entries yet. Add your first daily entry.");
        } else {
            DailyEarning last = all.get(all.size() - 1);
            fares.setAmount(last.getTotalFares());
            fuel.setAmount(last.getFuelCost());
            boundary.setAmount(last.getBoundaryFee());
            net.setAmount(last.getNetProfit(), true);
            net.setColor(last.isLoss() ? Theme.CORAL : Theme.MINT);
            status.setText("Latest entry: " + last.getDate() + (last.isLoss() ? "  (operating at a loss)" : ""));
        }
        chart.setData(ctx.earnings().latest(7));
        bestRoute.setText(ctx.bestRouteToday());
    }

    @Override
    public void onShown() {
        super.onShown();
        chart.replay();
    }
}
