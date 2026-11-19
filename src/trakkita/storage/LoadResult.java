package trakkita.storage;

import java.util.ArrayList;
import java.util.List;
import trakkita.model.DailyEarning;
import trakkita.model.FuelEntry;
import trakkita.model.TapSession;

/** Everything read from the file, plus any lines that had to be skipped. */
public class LoadResult {
    private final List<DailyEarning> earnings = new ArrayList<>();
    private final List<FuelEntry> fuel = new ArrayList<>();
    private final List<TapSession> sessions = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public List<DailyEarning> getEarnings() { return earnings; }
    public List<FuelEntry> getFuel() { return fuel; }
    public List<TapSession> getSessions() { return sessions; }
    public List<String> getWarnings() { return warnings; }
}
