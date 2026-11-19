package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import trakkita.exception.InvalidInputException;
import trakkita.gui.widget.CardPanel;
import trakkita.gui.widget.Theme;
import trakkita.gui.widget.ThemedButton;
import trakkita.model.DailyEarning;
import trakkita.service.AppContext;
import trakkita.util.Money;
import trakkita.util.Validator;

public class ProfitPanel extends ScreenPanel {
    private final JTextField date = UI.field(9);
    private final JTextField fares = UI.field(8);
    private final JTextField fuel = UI.field(8);
    private final JTextField boundary = UI.field(8);
    private final JTextField search = UI.field(9);
    private final JLabel warning = new JLabel(" ");
    private final DefaultTableModel model = UI.model("Date", "Total fares", "Fuel", "Boundary", "Net profit");
    private final JTable table = UI.table(model);
    private List<DailyEarning> shown = new ArrayList<>();

    public ProfitPanel(AppContext ctx) {
        super(ctx);
        CardPanel form = UI.card();
        ThemedButton add = UI.button("Add", true);
        ThemedButton update = UI.button("Update", false);
        ThemedButton delete = UI.button("Delete", Theme.DARK_RED, java.awt.Color.WHITE);
        ThemedButton clear = UI.button("Clear", Theme.CREAM, Theme.INK);
        UI.stack(form, 12,
                UI.row(UI.labeled("Date (yyyy-MM-dd)", date), UI.labeled("Total fares (PHP)", fares),
                        UI.labeled("Fuel cost (PHP)", fuel), UI.labeled("Boundary fee (PHP)", boundary),
                        UI.labeled("Search by date", search)),
                UI.row(add, update, delete, clear));

        warning.setFont(Theme.mono(13, true));
        warning.setForeground(Theme.DARK_RED);
        warning.setVisible(false);
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(header("Profit / Loss", "Net profit = total fares - fuel cost - boundary fee."), BorderLayout.NORTH);
        north.add(form, BorderLayout.CENTER);
        north.add(warning, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(UI.tableCard(table), BorderLayout.CENTER);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(Theme.mono(13, true));
                if (!sel && r < shown.size()) {
                    comp.setForeground(shown.get(r).isLoss() ? Theme.DARK_RED : Theme.TEAL);
                }
                return comp;
            }
        });

        date.setText(LocalDate.now().toString());
        add.addActionListener(e -> onAdd());
        update.addActionListener(e -> onUpdate());
        delete.addActionListener(e -> onDelete());
        clear.addActionListener(e -> clearForm());
        search.getDocument().addDocumentListener(UI.onChange(this::refresh));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });
    }

    /** Builds a validated entry from the form. Throws InvalidInputException on any bad field. */
    private DailyEarning readForm() {
        return new DailyEarning(
                Validator.parseDate(date.getText()),
                Validator.parseNumber(fares.getText(), "Total fares"),
                Validator.parseNumber(fuel.getText(), "Fuel cost"),
                Validator.parseNumber(boundary.getText(), "Boundary fee"));
    }

    private void onAdd() {
        try {
            DailyEarning e = readForm();
            ctx.earnings().add(e);
            persist();
            clearForm();
            refresh();
            warning.setText(e.isLoss()
                    ? "Loss warning: this day ended " + Money.peso(-e.getNetProfit()) + " below zero." : " ");
            warning.setVisible(e.isLoss());
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
            DailyEarning checked = readForm();          // validate everything before changing anything
            DailyEarning target = shown.get(row);
            target.setDate(checked.getDate());
            target.setTotalFares(checked.getTotalFares());
            target.setFuelCost(checked.getFuelCost());
            target.setBoundaryFee(checked.getBoundaryFee());
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
        if (UI.confirm(this, "Delete the selected entry?")) {
            ctx.earnings().remove(shown.get(row));
            persist();
            clearForm();
            refresh();
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < shown.size()) {
            DailyEarning e = shown.get(row);
            date.setText(e.getDate().toString());
            fares.setText(String.valueOf(e.getTotalFares()));
            fuel.setText(String.valueOf(e.getFuelCost()));
            boundary.setText(String.valueOf(e.getBoundaryFee()));
        }
    }

    private void clearForm() {
        date.setText(LocalDate.now().toString());
        fares.setText("");
        fuel.setText("");
        boundary.setText("");
        table.clearSelection();
    }

    @Override
    public void refresh() {
        String q = search.getText().trim();
        shown = ctx.earnings().filter(e -> q.isEmpty() || e.getDate().toString().contains(q));
        Collections.reverse(shown);                      // newest first
        model.setRowCount(0);
        for (DailyEarning e : shown) {
            model.addRow(new Object[] {e.getDate(), Money.peso(e.getTotalFares()), Money.peso(e.getFuelCost()),
                    Money.peso(e.getBoundaryFee()), Money.peso(e.getNetProfit())});
        }
    }
}
