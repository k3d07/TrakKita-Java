package trakkita.model;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import trakkita.exception.InvalidInputException;
import trakkita.util.Money;

/** One Quick-Tap fare-counting session. */
public class TapSession extends LogEntry {
    public static final double DISCOUNTED_FARE = 11.00;
    public static final double REGULAR_FARE = 13.00;

    private int discountedTaps;
    private int regularTaps;
    private final Deque<Boolean> history = new ArrayDeque<>(); // for undo only, not saved

    public TapSession(LocalDate date) {
        super(date);
    }

    public TapSession(LocalDate date, int discountedTaps, int regularTaps) {
        super(date);
        if (discountedTaps < 0 || regularTaps < 0) {
            throw new InvalidInputException("Tap counts cannot be negative.");
        }
        this.discountedTaps = discountedTaps;
        this.regularTaps = regularTaps;
    }

    public void tap(boolean discounted) {
        if (discounted) { discountedTaps++; } else { regularTaps++; }
        history.push(discounted);
    }

    /** Removes the most recent tap. Returns false if there is nothing to undo. */
    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        if (history.pop()) { discountedTaps--; } else { regularTaps--; }
        return true;
    }

    public int getDiscountedTaps() { return discountedTaps; }
    public int getRegularTaps() { return regularTaps; }
    public int getTapCount() { return discountedTaps + regularTaps; }

    public double getTotalCollected() {
        return discountedTaps * DISCOUNTED_FARE + regularTaps * REGULAR_FARE;
    }

    @Override public String getType() { return "TAP"; }

    @Override protected String fieldsToCsv() { return discountedTaps + "," + regularTaps; }

    @Override
    public String summary() {
        return getDate() + " | " + getTapCount() + " passengers (" + discountedTaps + " discounted, "
                + regularTaps + " regular) | " + Money.peso(getTotalCollected());
    }
}
