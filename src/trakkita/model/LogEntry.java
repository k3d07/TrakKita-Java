package trakkita.model;

import java.time.LocalDate;
import trakkita.exception.InvalidInputException;

/** Abstract parent of every dated record the app stores. */
public abstract class LogEntry implements Persistable, Reportable, Comparable<LogEntry> {
    private LocalDate date;

    protected LogEntry(LocalDate date) {
        setDate(date);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new InvalidInputException("Date is required.");
        }
        this.date = date;
    }

    /** Short tag written in the file, e.g. EARNING. */
    public abstract String getType();

    /** Comma-separated fields after the date. */
    protected abstract String fieldsToCsv();

    @Override
    public String toCsvLine() {
        return getType() + "," + date + "," + fieldsToCsv();
    }

    @Override
    public int compareTo(LogEntry other) {
        return date.compareTo(other.date);
    }
}
