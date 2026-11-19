package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import trakkita.exception.InvalidInputException;
import trakkita.gui.widget.Banner;
import trakkita.gui.widget.CardPanel;
import trakkita.gui.widget.CoverageGauge;
import trakkita.gui.widget.LeaderRow;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.model.PUV;
import trakkita.service.AppContext;
import trakkita.service.BreakEvenInput;
import trakkita.service.BreakEvenResult;
import trakkita.service.PUVCatalog;
import trakkita.util.Money;
import trakkita.util.Validator;

public class BreakEvenPanel extends ScreenPanel {
    private final JComboBox<PUV> vehicle = new JComboBox<>(PUVCatalog.all().toArray(new PUV[0]));
    private final JTextField fuelPrice = UI.field(7);
    private final JTextField efficiency = UI.field(7);
    private final JTextField distance = UI.field(7);
    private final JTextField trips = UI.field(7);
    private final JTextField boundary = UI.field(7);
    private final JTextField maintenance = UI.field(7);
    private final JTextField wages = UI.field(7);
    private final JTextField other = UI.field(7);
    private final JTextField fare = UI.field(7);
    private final JTextField pax = UI.field(7);

    private final LeaderRow fuelRow = new LeaderRow("Fuel cost", false, false);
    private final LeaderRow boundaryRow = new LeaderRow("Boundary fee", false, false);
    private final LeaderRow maintRow = new LeaderRow("Maintenance", false, false);
    private final LeaderRow extraRow = new LeaderRow("Wages + other", false, false);
    private final LeaderRow totalRow = new LeaderRow("TOTAL COST", true, true);
    private final LeaderRow grossRow = new LeaderRow("Gross revenue", false, false);
    private final LeaderRow netRow = new LeaderRow("NET PROFIT", true, false);
    private final LeaderRow beRow = new LeaderRow("Break-even", false, false);
    private final CoverageGauge gauge = new CoverageGauge();
    private final Banner banner = new Banner();

    public BreakEvenPanel(AppContext ctx) {
        super(ctx);
        vehicle.setFont(Theme.mono(14, false));
        vehicle.setPreferredSize(new java.awt.Dimension(vehicle.getPreferredSize().width + 20, 40));
        add(header("Break-Even Simulator",
                "Pick a vehicle to fill defaults, then adjust any value and press Simulate."), BorderLayout.NORTH);

        CardPanel form = UI.ticket("Inputs", Theme.INDIGO, Theme.CREAM);
        JPanel grid = new JPanel(new GridLayout(0, 2, 14, 12));
        grid.setOpaque(false);
        form.setLayout(new BorderLayout());
        form.add(grid, BorderLayout.NORTH);
        grid.add(UI.labeled("Vehicle type", vehicle));
        grid.add(UI.labeled("Fuel price (PHP/L)", fuelPrice));
        grid.add(UI.labeled("Efficiency (km/L)", efficiency));
        grid.add(UI.labeled("Distance for the day (km)", distance));
        grid.add(UI.labeled("Trips per day", trips));
        grid.add(UI.labeled("Boundary fee (PHP)", boundary));
        grid.add(UI.labeled("Maintenance (PHP)", maintenance));
        grid.add(UI.labeled("Driver wages (PHP)", wages));
        grid.add(UI.labeled("Other costs (PHP)", other));
        grid.add(UI.labeled("Fare per passenger (PHP)", fare));
        grid.add(UI.labeled("Passengers per trip", pax));
        ThemedButton simulate = UI.button("Simulate", true);
        JPanel simBox = new JPanel(new BorderLayout());
        simBox.setOpaque(false);
        simBox.add(Box.createVerticalStrut(18), BorderLayout.NORTH);
        simBox.add(simulate, BorderLayout.CENTER);
        grid.add(simBox);

        CardPanel receipt = UI.card().background(Theme.PAPER).dashed(true);
        JLabel title = new JLabel("BREAK-EVEN RECEIPT", JLabel.CENTER);
        title.setFont(Theme.mono(15, true));
        title.setForeground(Theme.INK);
        JPanel rows = new JPanel();
        rows.setOpaque(false);
        UI.stack(rows, 4, title, fuelRow, boundaryRow, maintRow, extraRow, totalRow, grossRow, netRow, beRow);
        title.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        title.setPreferredSize(new java.awt.Dimension(200, 30));
        JPanel lower = new JPanel(new BorderLayout(0, 10));
        lower.setOpaque(false);
        lower.add(gauge, BorderLayout.NORTH);
        lower.add(banner, BorderLayout.SOUTH);
        receipt.setLayout(new BorderLayout(0, 12));
        receipt.add(rows, BorderLayout.NORTH);
        receipt.add(lower, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 18, 0));
        center.setOpaque(false);
        center.add(form);
        center.add(receipt);
        add(center, BorderLayout.CENTER);

        fuelPrice.setText("60.0");
        distance.setText("120");
        trips.setText("10");
        wages.setText("0");
        other.setText("50");
        fare.setText("13.0");
        pax.setText("12");
        applyDefaults();
        vehicle.addActionListener(e -> applyDefaults());
        simulate.addActionListener(e -> onSimulate());
        onSimulate();
    }

    /** Polymorphism: each PUV subclass answers with its own numbers. */
    private void applyDefaults() {
        PUV p = (PUV) vehicle.getSelectedItem();
        if (p == null) {
            return;
        }
        efficiency.setText(String.valueOf(p.getFuelEfficiencyKmPerL()));
        boundary.setText(String.valueOf(p.getDefaultBoundaryFee()));
        maintenance.setText(String.valueOf(p.getDefaultMaintenance()));
    }

    private void onSimulate() {
        try {
            BreakEvenInput in = new BreakEvenInput(
                    Validator.parseNumber(fuelPrice.getText(), "Fuel price"),
                    Validator.parseNumber(efficiency.getText(), "Fuel efficiency"),
                    Validator.parseNumber(distance.getText(), "Distance"),
                    Validator.parseWhole(trips.getText(), "Trips per day"),
                    Validator.parseNumber(boundary.getText(), "Boundary fee"),
                    Validator.parseNumber(maintenance.getText(), "Maintenance"),
                    Validator.parseNumber(wages.getText(), "Driver wages"),
                    Validator.parseNumber(other.getText(), "Other costs"),
                    Validator.parseNumber(fare.getText(), "Fare per passenger"),
                    Validator.parseWhole(pax.getText(), "Passengers per trip"));
            show(in, ctx.breakEven().calculate(in));
        } catch (InvalidInputException ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private void show(BreakEvenInput in, BreakEvenResult r) {
        Color ink = Theme.INK;
        fuelRow.setValue(Money.peso(r.getFuelCost()), ink);
        boundaryRow.setValue(Money.peso(in.getBoundaryFee()), ink);
        maintRow.setValue(Money.peso(in.getMaintenance()), ink);
        extraRow.setValue(Money.peso(in.getDriverWages() + in.getOtherCosts()), ink);
        totalRow.setValue(Money.peso(r.getTotalCost()), ink);
        grossRow.setValue(Money.peso(r.getGrossRevenue()), ink);
        netRow.setValue(Money.peso(r.getNetProfit()), r.isLoss() ? Theme.DARK_RED : Theme.TEAL);
        beRow.setValue(r.getBreakEvenPax() + " pax (" + r.getBreakEvenPerTrip() + "/trip)", ink);
        gauge.setCoverage(r.getCoveragePct(), r.isLoss());
        banner.setMessage(r.isLoss()
                ? "Operating at a loss: " + r.getShortfallPax() + " more passengers needed to break even."
                : "Profitable: your passengers cover all costs.", r.isLoss());
    }

    @Override
    public void refresh() {
        // inputs stay as the user left them; nothing to reload
    }
}
