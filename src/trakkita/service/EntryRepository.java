package trakkita.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import trakkita.model.LogEntry;

/** Generic in-memory store for any LogEntry subclass (ArrayList inside). */
public class EntryRepository<T extends LogEntry> {
    private final List<T> items = new ArrayList<>();

    public void add(T entry) {
        items.add(Objects.requireNonNull(entry, "entry"));
    }

    public boolean remove(T entry) {
        return items.remove(entry);
    }

    public void clear() {
        items.clear();
    }

    public int size() {
        return items.size();
    }

    /** Returns a copy sorted by date (oldest first). */
    public List<T> getAll() {
        List<T> copy = new ArrayList<>(items);
        Collections.sort(copy);
        return copy;
    }

    public List<T> filter(Predicate<T> rule) {
        List<T> out = new ArrayList<>();
        for (T e : getAll()) {
            if (rule.test(e)) {
                out.add(e);
            }
        }
        return out;
    }

    public Optional<T> findByDate(LocalDate date) {
        return getAll().stream().filter(e -> e.getDate().equals(date)).findFirst();
    }

    /** The newest n entries, oldest first. */
    public List<T> latest(int n) {
        List<T> all = getAll();
        return new ArrayList<>(all.subList(Math.max(0, all.size() - n), all.size()));
    }
}
