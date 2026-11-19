package trakkita.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import trakkita.exception.DataCorruptedException;
import trakkita.model.DailyEarning;
import trakkita.model.FuelEntry;
import trakkita.model.PUV;
import trakkita.model.Persistable;
import trakkita.model.TapSession;
import trakkita.model.TraditionalJeepney;
import trakkita.storage.DataStore;
import trakkita.storage.LoadResult;
import trakkita.util.Money;
import trakkita.util.Validator;

/** Facade the GUI talks to: holds the repositories, services and the data store. */
public class AppContext {
    private final DataStore store;
    private final EntryRepository<DailyEarning> earnings = new EntryRepository<>();
    private final EntryRepository<FuelEntry> fuel = new EntryRepository<>();
    private final EntryRepository<TapSession> sessions = new EntryRepository<>();
    private final FuelLogService fuelLog = new FuelLogService(fuel);
    private final RouteRecommender recommender = new RouteRecommender();
    private final BreakEvenCalculator breakEven = new BreakEvenCalculator();
    private final List<String> startupWarnings = new ArrayList<>();

    private PUV vehicle = new TraditionalJeepney();
    private double dieselPrice = 60.0;   // placeholder default
    private double boundaryFee = vehicle.getDefaultBoundaryFee();

    public AppContext(DataStore store) {
        this.store = store;
    }

    public void load() {
        try {
            LoadResult r = store.load();
            r.getEarnings().forEach(earnings::add);
            r.getFuel().forEach(fuel::add);
            r.getSessions().forEach(sessions::add);
            startupWarnings.addAll(r.getWarnings());
        } catch (DataCorruptedException e) {
            startupWarnings.add(e.getMessage() + " Starting with empty data.");
        }
    }

    public void save() throws IOException {
        List<Persistable> all = new ArrayList<>();
        all.addAll(earnings.getAll());
        all.addAll(fuel.getAll());
        all.addAll(sessions.getAll());
        store.save(all);
    }

    public EntryRepository<DailyEarning> earnings() { return earnings; }
    public EntryRepository<FuelEntry> fuel() { return fuel; }
    public EntryRepository<TapSession> sessions() { return sessions; }
    public FuelLogService fuelLog() { return fuelLog; }
    public RouteRecommender recommender() { return recommender; }
    public BreakEvenCalculator breakEven() { return breakEven; }
    public List<String> getStartupWarnings() { return startupWarnings; }

    public PUV getVehicle() { return vehicle; }
    public double getDieselPrice() { return dieselPrice; }
    public double getBoundaryFee() { return boundaryFee; }

    public void setRouteSettings(PUV vehicle, double dieselPrice, double boundaryFee) {
        this.vehicle = vehicle;
        this.dieselPrice = Validator.requirePositive(dieselPrice, "Diesel price");
        this.boundaryFee = Validator.requireNonNegative(boundaryFee, "Boundary fee");
    }

    public String bestRouteToday() {
        List<RouteResult> ranked = recommender.rank(vehicle, dieselPrice, boundaryFee);
        return ranked.get(0).getRoute().getName();
    }

    /** Plain-text summary report for export. */
    public String buildReport() {
        StringBuilder sb = new StringBuilder("TRAKKITA SUMMARY REPORT\n=======================\n");
        List<DailyEarning> all = earnings.getAll();
        if (all.isEmpty()) {
            sb.append("No daily entries yet.\n");
        } else {
            double fares = 0, fuelCost = 0, boundary = 0;
            for (DailyEarning e : all) {
                fares += e.getTotalFares();
                fuelCost += e.getFuelCost();
                boundary += e.getBoundaryFee();
            }
            Comparator<DailyEarning> byNet = Comparator.comparingDouble(DailyEarning::getNetProfit);
            sb.append("Days recorded: ").append(all.size()).append('\n')
              .append("Total fares:   ").append(Money.peso(fares)).append('\n')
              .append("Total fuel:    ").append(Money.peso(fuelCost)).append('\n')
              .append("Total boundary:").append(' ').append(Money.peso(boundary)).append('\n')
              .append("Net profit:    ").append(Money.peso(fares - fuelCost - boundary)).append('\n')
              .append("Best day:      ").append(Collections.max(all, byNet).summary()).append('\n')
              .append("Worst day:     ").append(Collections.min(all, byNet).summary()).append("\n\n");
        }
        sb.append("FUEL\n").append(fuelLog.summaryText()).append("\n\nQUICK-TAP SESSIONS\n");
        for (TapSession s : sessions.getAll()) {
            sb.append(s.summary()).append('\n');
        }
        return sb.toString();
    }
}
