package trakkita.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JFileChooser;
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
        update.setEnabled(false);
        delete.setEnabled(false);
        // CHANGED: "Search by date" is no longer in the entry form (it moved to the
        // history bar below)
        UI.stack(form, 12,
                UI.row(UI.labeled("Date (yyyy-MM-dd)", date), UI.labeled("Total fares (PHP)", fares),
                        UI.labeled("Fuel cost (PHP)", fuel), UI.labeled("Boundary fee (PHP)", boundary)),
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

        // CHANGED: history bar (title + search) sits directly above the table
        JPanel history = new JPanel(new BorderLayout(0, 8));
        history.setOpaque(false);
        history.add(buildHistoryBar(), BorderLayout.NORTH);
        history.add(UI.tableCard(table), BorderLayout.CENTER);
        add(history, BorderLayout.CENTER);

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

        // CHANGED: align the columns (must stay AFTER the Net profit renderer above)
        TableStyle.padLeftColumn(table, 0); // Date
        TableStyle.rightAlignColumns(table, 1, 2, 3, 4); // Total fares, Fuel, Boundary, Net profit

        date.setText(LocalDate.now().toString());
        add.addActionListener(e -> onAdd());
        update.addActionListener(e -> onUpdate());
        delete.addActionListener(e -> onDelete());
        clear.addActionListener(e -> clearForm());
        search.getDocument().addDocumentListener(UI.onChange(this::refresh));
        // NEW (Part 2): when the date changes, fill Fuel cost from the Fuel Log if it
        // is empty
        date.getDocument().addDocumentListener(UI.onChange(this::suggestFuel));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
                boolean selected = table.getSelectedRow() >= 0;
                update.setEnabled(selected);
                delete.setEnabled(selected);
            }
        });
        UI.bindEnter(this::onAdd, date, fares, fuel, boundary);
        UI.bindEscape(this, this::clearForm);
    }

    /**
     * NEW: title on the left, "Search by date" on the right, right above the table.
     */
    private JPanel buildHistoryBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        JLabel title = new JLabel("HISTORY");
        title.setFont(Theme.mono(13, true));
        title.setForeground(Theme.INK);
        title.setVerticalAlignment(SwingConstants.BOTTOM);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(UI.labeled("Search by date", search));
        ThemedButton export = UI.button("Export CSV", Theme.CREAM, Theme.INK);
        export.addActionListener(e -> exportCsv());
        right.add(export);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /**
     * Builds a validated entry from the form. Throws InvalidInputException on any
     * bad field.
     */
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
            List<DailyEarning> sameDate = ctx.earnings().filter(existing -> e.getDate().equals(existing.getDate()));
            if (!sameDate.isEmpty()) {
                if (!UI.confirm(this, "An entry for " + e.getDate() + " already exists. Replace it?")) {
                    return;
                }
                DailyEarning existing = sameDate.get(0);
                existing.setTotalFares(e.getTotalFares());
                existing.setFuelCost(e.getFuelCost());
                existing.setBoundaryFee(e.getBoundaryFee());
                persist();
                clearForm();
                refresh();
                return;
            }
            ctx.earnings().add(e);
            persist();
            clearForm();
            refresh();
            warning.setText(e.isLoss()
                    ? "Loss warning: this day ended " + Money.peso(-e.getNetProfit()) + " below zero."
                    : " ");
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
            DailyEarning checked = readForm(); // validate everything before changing anything
            DailyEarning target = shown.get(table.convertRowIndexToModel(row));
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
            ctx.earnings().remove(shown.get(table.convertRowIndexToModel(row)));
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

    /**
     * NEW (Part 2): runs every time this screen opens. Picks up a total sent from
     * Quick-Tap.
     */
    @Override
    public void onShown() {
        if (!ctx.hasPendingEntry()) {
            return;
        }
        LocalDate d = ctx.takePendingDate();
        double amount = ctx.takePendingFares();

        table.clearSelection();
        fuel.setText(""); // so the Fuel Log suggestion can fill it
        date.setText(d.toString());
        fares.setText(String.format(java.util.Locale.US, "%.2f", amount));
        double lastBoundary = ctx.lastBoundaryFee(); // boundary rarely changes day to day
        boundary.setText(lastBoundary > 0 ? String.format(java.util.Locale.US, "%.2f", lastBoundary) : "");

        boolean exists = !ctx.earnings().filter(e -> d.equals(e.getDate())).isEmpty();
        if (exists) {
            UI.info(this, "An entry for " + d + " already exists.\n"
                    + "Select it in the table and press Update, or change the date.");
        } else {
            suggestFuel();
            fuel.requestFocusInWindow(); // fuel cost is the next thing to type
        }
    }

    /**
     * NEW (Part 2): fills Fuel cost from that day's Fuel Log entries, only if the
     * field is empty.
     */
    private void suggestFuel() {
        if (!fuel.getText().isBlank()) {
            return; // never overwrite what the driver typed
        }
        try {
            double cost = ctx.fuelCostForDate(Validator.parseDate(date.getText()));
            if (cost > 0) {
                fuel.setText(String.format(java.util.Locale.US, "%.2f", cost));
            }
        } catch (InvalidInputException ignored) {
            // the date is still being typed, do nothing
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("trakkita_profit_loss.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        StringBuilder csv = new StringBuilder("Date,Total fares (PHP),Fuel cost (PHP),Boundary fee (PHP),Net profit (PHP)\r\n");
        for (DailyEarning entry : shown) {
            csv.append(entry.getDate()).append(',')
                    .append(String.format(java.util.Locale.US, "%.2f", entry.getTotalFares())).append(',')
                    .append(String.format(java.util.Locale.US, "%.2f", entry.getFuelCost())).append(',')
                    .append(String.format(java.util.Locale.US, "%.2f", entry.getBoundaryFee())).append(',')
                    .append(String.format(java.util.Locale.US, "%.2f", entry.getNetProfit())).append("\r\n");
        }
        try {
            Files.writeString(chooser.getSelectedFile().toPath(), csv.toString(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            statusLine.showMessage("CSV exported.");
        } catch (IOException ex) {
            UI.error(this, "Could not export CSV: " + ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        String q = search.getText().trim();
        shown = ctx.earnings().filter(e -> q.isEmpty() || e.getDate().toString().contains(q));
        Collections.reverse(shown); // newest first
        model.setRowCount(0);
        for (DailyEarning e : shown) {
            model.addRow(new Object[] { e.getDate(), Money.peso(e.getTotalFares()), Money.peso(e.getFuelCost()),
                    Money.peso(e.getBoundaryFee()), Money.peso(e.getNetProfit()) });
        }
    }
}
