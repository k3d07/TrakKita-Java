package trakkita.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import trakkita.model.FuelEntry;
import trakkita.util.Money;

public class FuelLogService {
    public static final int WINDOW = 7;
    private final EntryRepository<FuelEntry> repo;

    public FuelLogService(EntryRepository<FuelEntry> repo) {
        this.repo = repo;
    }

    public List<FuelEntry> lastSevenDays() {
        return repo.latest(WINDOW);
    }

    public String summaryText() {
        List<FuelEntry> w = lastSevenDays();
        if (w.isEmpty()) {
            return "No fuel entries yet.";
        }
        double liters = 0;
        double cost = 0;
        for (FuelEntry e : w) {
            liters += e.getLiters();
            cost += e.getTotalCost();
        }
        Comparator<FuelEntry> byCost = Comparator.comparingDouble(FuelEntry::getTotalCost);
        FuelEntry max = Collections.max(w, byCost);
        FuelEntry min = Collections.min(w, byCost);
        return String.format("Last %d entries | avg %.2f L/day, %s/day%nMost expensive: %s (%s)   Cheapest: %s (%s)",
                w.size(), liters / w.size(), Money.peso(cost / w.size()),
                max.getDate(), Money.peso(max.getTotalCost()),
                min.getDate(), Money.peso(min.getTotalCost()));
    }
}
