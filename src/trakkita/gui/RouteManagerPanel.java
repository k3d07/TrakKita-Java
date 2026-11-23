package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import trakkita.exception.InvalidInputException;
import trakkita.gui.widget.CardPanel;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.model.Route;
import trakkita.service.AppContext;
import trakkita.util.Money;
import trakkita.util.Validator;

/** CRUD screen for the routes stored in data/routes.csv. */
public class RouteManagerPanel extends ScreenPanel {
    private final JTextField name = UI.field(18);
    private final JTextField distance = UI.field(7);
    private final JTextField traffic = UI.field(7);
    private final JTextField fare = UI.field(8);
    private final JTextField trips = UI.field(7);
    private final DefaultTableModel model = UI.model("ID", "Route", "Distance km", "Traffic", "Fare/trip", "Trips/day");
    private final JTable table = UI.table(model);
    private List<Route> shown = new ArrayList<>();

    public RouteManagerPanel(AppContext ctx) {
        super(ctx);
        CardPanel form = UI.card();
        ThemedButton add = UI.button("Add", true);
        ThemedButton update = UI.button("Update", false);
        ThemedButton delete = UI.button("Delete", Theme.DARK_RED, Color.WHITE);
        ThemedButton clear = UI.button("Clear", Theme.CREAM, Theme.INK);
        ThemedButton save = UI.button("Save routes", Theme.TEAL, Color.WHITE);
        update.setEnabled(false);
        delete.setEnabled(false);
        UI.stack(form, 10,
                UI.row(UI.labeled("Route name", name), UI.labeled("Distance (km)", distance),
                        UI.labeled("Traffic factor", traffic)),
                UI.row(UI.labeled("Average fare per trip (PHP)", fare), UI.labeled("Trips per day", trips),
                        add, update, delete, clear, save));
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(header("Manage Routes", "Changes are saved to data/routes.csv."), BorderLayout.NORTH);
        north.add(form, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);
        add(UI.tableCard(table), BorderLayout.CENTER);
        TableStyle.padLeftColumn(table, 1);
        TableStyle.rightAlignColumns(table, 0, 2, 3, 4, 5);
        TableStyle.setColumnWidths(table, 50, 330, 110, 90, 110, 90);

        add.addActionListener(e -> onAdd());
        update.addActionListener(e -> onUpdate());
        delete.addActionListener(e -> onDelete());
        clear.addActionListener(e -> clearForm());
        save.addActionListener(e -> saveRoutes());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
            boolean selected = table.getSelectedRow() >= 0;
            update.setEnabled(selected);
            delete.setEnabled(selected);
        });
        UI.bindEnter(this::onAdd, name, distance, traffic, fare, trips);
        UI.bindEscape(this, this::clearForm);
    }

    private Route readForm(int id) {
        return new Route(id, name.getText(), Validator.parseNumber(distance.getText(), "Distance"),
                Validator.parseNumber(traffic.getText(), "Traffic factor"),
                Validator.parseNumber(fare.getText(), "Average fare per trip"),
                Validator.parseWhole(trips.getText(), "Trips per day"));
    }

    private void onAdd() {
        try {
            int id = ctx.recommender().getRoutes().stream().mapToInt(Route::getId).max().orElse(0) + 1;
            List<Route> routes = ctx.recommender().getRoutes();
            routes.add(readForm(id));
            ctx.recommender().replaceRoutes(routes);
            saveRoutes();
            refresh();
            clearForm();
        } catch (InvalidInputException ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) { UI.info(this, "Select a route first."); return; }
        try {
            int modelRow = table.convertRowIndexToModel(row);
            Route target = shown.get(modelRow);
            Route checked = readForm(target.getId());
            target.setName(checked.getName());
            target.setDistanceKm(checked.getDistanceKm());
            target.setTrafficFactor(checked.getTrafficFactor());
            target.setAvgFarePerTrip(checked.getAvgFarePerTrip());
            target.setTripsPerDay(checked.getTripsPerDay());
            saveRoutes();
            refresh();
        } catch (InvalidInputException ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { UI.info(this, "Select a route first."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        Route target = shown.get(modelRow);
        if (shown.size() <= 1) {
            UI.info(this, "Keep at least one route in the recommender.");
            return;
        }
        if (!UI.confirm(this, "Delete route '" + target.getName() + "'?")) return;
        List<Route> routes = ctx.recommender().getRoutes();
        routes.remove(target);
        ctx.recommender().replaceRoutes(routes);
        saveRoutes();
        refresh();
        clearForm();
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Route r = shown.get(table.convertRowIndexToModel(row));
        name.setText(r.getName());
        distance.setText(String.valueOf(r.getDistanceKm()));
        traffic.setText(String.valueOf(r.getTrafficFactor()));
        fare.setText(String.valueOf(r.getAvgFarePerTrip()));
        trips.setText(String.valueOf(r.getTripsPerDay()));
    }

    private void clearForm() {
        name.setText(""); distance.setText(""); traffic.setText(""); fare.setText(""); trips.setText("");
        table.clearSelection();
    }

    private void saveRoutes() {
        try {
            ctx.recommender().saveRoutes();
            statusLine.showMessage("Routes saved to data/routes.csv.");
        } catch (IOException ex) {
            UI.error(this, "Could not save routes: " + ex.getMessage());
            statusLine.showMessage("Could not save routes.", true);
        }
    }

    @Override public void refresh() {
        shown = ctx.recommender().getRoutes();
        model.setRowCount(0);
        for (Route r : shown) {
            model.addRow(new Object[] {r.getId(), r.getName(), String.format(java.util.Locale.US, "%.1f", r.getDistanceKm()),
                    String.format(java.util.Locale.US, "%.1f", r.getTrafficFactor()), Money.peso(r.getAvgFarePerTrip()),
                    r.getTripsPerDay()});
        }
    }
}
