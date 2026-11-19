package trakkita.gui.widget;

/** A number that glides toward its target value (used for the money counters). */
public class CountUp {
    private double shown;
    private double target;

    public void set(double value, boolean animate) {
        target = value;
        if (!animate || Anim.isInstant()) {
            shown = value;
        }
    }

    /** Advances one frame. Returns true if the value changed. */
    public boolean step() {
        if (shown == target) {
            return false;
        }
        shown += (target - shown) * 0.18;
        if (Math.abs(target - shown) < 0.5) {
            shown = target;
        }
        return true;
    }

    public double get() { return shown; }
}
