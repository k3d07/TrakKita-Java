package trakkita.gui;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JComboBox;
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
    private final JLabel best = new JLabel(" ");
    private final DefaultTableModel model = UI.model("Rank", "Route", "Fuel cost", "Gross", "Net income", "Score");
    private final JTable table = UI.table(model);

    public RoutePanel(AppContext ctx) {
        super(ctx);
        vehicle.setFont(Theme.mono(14, false));
        vehicle.setPreferredSize(new java.awt.Dimension(vehicle.getPreferredSize().width + 20, 40));
        diesel.setText(String.valueOf(ctx.getDieselPrice()));
        boundary.setText(String.valueOf(ctx.getBoundaryFee()));
        ThemedButton rank = UI.button("Rank routes", true);

        CardPanel form = UI.card();
        UI.stack(form, 0, UI.row(UI.labeled("Vehicle type", vehicle), UI.labeled("Diesel price (PHP/L)", diesel),
                UI.labeled("Boundary fee (PHP)", boundary), rank));

        best.setFont(Theme.mono(15, true));
        best.setForeground(Theme.TEAL);
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(header("Route Recommender", "Score = net income / fuel cost. Higher is better."), BorderLayout.NORTH);
        north.add(form, BorderLayout.CENTER);
        north.add(best, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(UI.tableCard(table), BorderLayout.CENTER);

        // choosing a vehicle fills in that vehicle's default boundary fee (polymorphic call)
        vehicle.addActionListener(e -> {
            PUV p = (PUV) vehicle.getSelectedItem();
            if (p != null) {
                boundary.setText(String.valueOf(p.getDefaultBoundaryFee()));
            }
        });
        rank.addActionListener(e -> onRank());
    }

    private void onRank() {
        try {
            PUV p = (PUV) vehicle.getSelectedItem();
            double d = Validator.parseNumber(diesel.getText(), "Diesel price");
            double b = Validator.parseNumber(boundary.getText(), "Boundary fee");
            ctx.setRouteSettings(p, d, b);
            refresh();
        } catch (InvalidInputException ex) {
            UI.error(this, ex.getMessage());
        }
    }

    @Override
    public void refresh() {
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
}
