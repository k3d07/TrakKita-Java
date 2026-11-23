package trakkita.gui;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import trakkita.exception.InvalidInputException;
import trakkita.gui.widget.CardPanel;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.model.PUV;
import trakkita.service.AppContext;
import trakkita.service.PUVCatalog;
import trakkita.service.RouteResult;
import trakkita.util.Money;
import trakkita.util.Validator;

public class RoutePanel extends ScreenPanel {
    private final JComboBox<PUV> vehicle = new JComboBox<>(PUVCatalog.all().toArray(new PUV[0]));
    private final JTextField diesel = UI.field(7);
    private final JTextField boundary = UI.field(7);
    private final JCheckBox ownerOperator = new JCheckBox("I own this vehicle (no boundary fee)");
    private final JLabel best = new JLabel(" ");
    private final DefaultTableModel model = UI.model("Rank", "Route", "Fuel cost", "Gross", "Net income", "Score");
    private final JTable table = UI.table(model);

    public RoutePanel(AppContext ctx) {
        super(ctx);
        vehicle.setFont(Theme.mono(14, false));
        vehicle.setPreferredSize(new java.awt.Dimension(vehicle.getPreferredSize().width + 20, 40));
        diesel.setText(plain(ctx.getDieselPrice()));
        boundary.setText(plain(ctx.getBoundaryFee()));
        ownerOperator.setOpaque(false);
        ownerOperator.setFont(Theme.body(14));
        ownerOperator.setForeground(Theme.INK);
        ownerOperator.setSelected(ctx.isOwnerOperator());
        ThemedButton rank = UI.button("Rank routes", true);
        ThemedButton manage = UI.button("Manage routes", Theme.CREAM, Theme.INK);

        CardPanel form = UI.card();
        UI.stack(form, 0, UI.row(UI.labeled("Vehicle type", vehicle), UI.labeled("Diesel price (PHP/L)", diesel),
                UI.labeled("Boundary fee (PHP)", boundary), ownerOperator, rank, manage));

        best.setFont(Theme.mono(15, true));
        best.setForeground(Theme.TEAL);
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(header("Route Recommender", "Score = net income / fuel cost. Higher is better."), BorderLayout.NORTH);
        north.add(form, BorderLayout.CENTER);
        north.add(best, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(UI.tableCard(table), BorderLayout.CENTER);
        TableStyle.rightAlignColumns(table, 0, 2, 3, 4, 5);
        TableStyle.setColumnWidths(table, 60, 330, 120, 120, 120, 80);

        // choosing a vehicle fills in that vehicle's default boundary fee (polymorphic call)
        vehicle.addActionListener(e -> {
            PUV p = (PUV) vehicle.getSelectedItem();
            if (p != null) {
                ctx.setVehicle(p);
                boundary.setText(plain(ctx.getBoundaryFee()));
            }
        });
        ownerOperator.addActionListener(e -> {
            ctx.setOwnerOperator(ownerOperator.isSelected());
            boundary.setEnabled(!ownerOperator.isSelected());
            boundary.setText(plain(ctx.getBoundaryFee()));
        });
        rank.addActionListener(e -> onRank());
        manage.addActionListener(e -> goTo("Manage Routes"));
        UI.bindEnter(this::onRank, diesel, boundary);
    }

    private void onRank() {
        try {
            PUV p = (PUV) vehicle.getSelectedItem();
            double d = Validator.parseNumber(diesel.getText(), "Diesel price");
            double b = Validator.parseNumber(boundary.getText(), "Boundary fee");
            ctx.setRouteSettings(p, d, b, ownerOperator.isSelected());
            refresh();
        } catch (InvalidInputException ex) {
            UI.error(this, ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        PUV selected = ctx.getVehicle();
        for (int i = 0; i < vehicle.getItemCount(); i++) {
            if (vehicle.getItemAt(i).getClass().equals(selected.getClass())) {
                vehicle.setSelectedIndex(i);
                break;
            }
        }
        diesel.setText(plain(ctx.getDieselPrice()));
        boundary.setText(plain(ctx.getBoundaryFee()));
        ownerOperator.setSelected(ctx.isOwnerOperator());
        boundary.setEnabled(!ctx.isOwnerOperator());
        model.setRowCount(0);
        List<RouteResult> ranked = ctx.recommender().rank(ctx.getVehicle(), ctx.getDieselPrice(), ctx.getBoundaryFee());
        int rank = 1;
        for (RouteResult r : ranked) {
            model.addRow(new Object[] {rank++, r.getRoute().getName(), Money.peso(r.getFuelCost()),
                    Money.peso(r.getGross()), Money.peso(r.getNet()), String.format("%.2f", r.getScore())});
        }
        best.setText("Recommended today: " + ranked.get(0).getRoute().getName());
        table.setRowSelectionInterval(0, 0);
    }

    private static String plain(double value) {
        return new java.math.BigDecimal(value).setScale(4, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }
}
