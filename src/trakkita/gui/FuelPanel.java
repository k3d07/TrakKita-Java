package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import trakkita.exception.InvalidInputException;
import trakkita.gui.widget.CardPanel;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.model.FuelEntry;
import trakkita.service.AppContext;
import trakkita.util.Money;
import trakkita.util.Validator;

public class FuelPanel extends ScreenPanel {
    private final JTextField date = UI.field(9);
    private final JTextField liters = UI.field(7);
    private final JTextField price = UI.field(8);
    private final JLabel summary = new JLabel(" ");
    private final DefaultTableModel model = UI.model("Date", "Liters", "Price per liter", "Total cost");
    private final JTable table = UI.table(model);
    private List<FuelEntry> shown = new ArrayList<>();

    public FuelPanel(AppContext ctx) {
        super(ctx);
        ThemedButton add = UI.button("Add", true);
        ThemedButton update = UI.button("Update", false);
        ThemedButton delete = UI.button("Delete", Theme.DARK_RED, Color.WHITE);
        CardPanel form = UI.card();
        UI.stack(form, 12,
                UI.row(UI.labeled("Date (yyyy-MM-dd)", date), UI.labeled("Liters", liters),
                        UI.labeled("Price per liter (PHP)", price)),
                UI.row(add, update, delete));

        CardPanel summaryCard = UI.ticket("7-entry summary", Theme.TEAL, Color.WHITE);
        summaryCard.setLayout(new BorderLayout());
        summary.setFont(Theme.mono(13, false));
        summary.setForeground(Theme.INK);
        summaryCard.add(summary, BorderLayout.CENTER);

        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(header("Fuel Log", "Log each fill-up. The summary covers your latest 7 entries."), BorderLayout.NORTH);
        north.add(form, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);
        add(UI.tableCard(table), BorderLayout.CENTER);
        add(summaryCard, BorderLayout.SOUTH);

        date.setText(LocalDate.now().toString());
        add.addActionListener(e -> onAdd());
        update.addActionListener(e -> onUpdate());
        delete.addActionListener(e -> onDelete());
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && r >= 0 && r < shown.size()) {
                FuelEntry f = shown.get(r);
                date.setText(f.getDate().toString());
                liters.setText(String.valueOf(f.getLiters()));
                price.setText(String.valueOf(f.getPricePerLiter()));
            }
        });
    }

    private FuelEntry readForm() {
        return new FuelEntry(Validator.parseDate(date.getText()),
                Validator.parseNumber(liters.getText(), "Liters"),
                Validator.parseNumber(price.getText(), "Price per liter"));
    }

    private void onAdd() {
        try {
            ctx.fuel().add(readForm());
            persist();
            liters.setText("");
            price.setText("");
            refresh();
        } catch (InvalidInputException ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.info(this, "Select a row in the table first.");
            return;
        }
        try {
            FuelEntry checked = readForm();
            FuelEntry target = shown.get(row);
            target.setDate(checked.getDate());
            target.setLiters(checked.getLiters());
            target.setPricePerLiter(checked.getPricePerLiter());
            persist();
            refresh();
        } catch (InvalidInputException ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.info(this, "Select a row in the table first.");
            return;
        }
        if (UI.confirm(this, "Delete the selected fuel entry?")) {
            ctx.fuel().remove(shown.get(row));
            persist();
            refresh();
        }
    }

    @Override
    public void refresh() {
        shown = ctx.fuel().getAll();
        Collections.reverse(shown);
        model.setRowCount(0);
        for (FuelEntry f : shown) {
            model.addRow(new Object[] {f.getDate(), String.format("%.2f", f.getLiters()),
                    Money.peso(f.getPricePerLiter()), Money.peso(f.getTotalCost())});
        }
        summary.setText("<html>" + ctx.fuelLog().summaryText().replace("\n", "<br>") + "</html>");
    }
}
