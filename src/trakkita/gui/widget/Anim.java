package trakkita.gui.widget;

import java.awt.event.HierarchyEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import javax.swing.JComponent;
import javax.swing.Timer;

/** One shared 60 fps clock for all animations. Widgets only tick while they are on screen. */
public final class Anim {
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();
    private static final Timer TIMER = new Timer(16, e -> {
        for (Runnable r : LISTENERS) {
            r.run();
        }
    });
    private static long offset;
    private static boolean instant;

    private Anim() { }

    public static long now() {
        return System.nanoTime() / 1_000_000L + offset;
    }

    /** Progress 0..1 of an animation that started at start (ms) and lasts duration (ms). */
    public static double progress(long start, long duration) {
        if (instant) {
            return 1.0;
        }
        return Theme.clamp((now() - start) / (double) duration);
    }

    /** Position 0..1 inside a repeating cycle of periodMs. */
    public static double loop(long periodMs) {
        return instant ? 0.25 : (now() % periodMs) / (double) periodMs;
    }

    /** For tests/screenshots: makes every animation jump to its end state. */
    public static void setInstant(boolean value) {
        instant = value;
    }

    public static boolean isInstant() {
        return instant;
    }

    /**
     * Makes component c repaint on every tick while it is displayed and needsRepaint says so.
     * The supplier is also where a widget advances its own animation state.
     */
    public static void bind(JComponent c, BooleanSupplier needsRepaint) {
        Runnable tick = () -> {
            if (c.isShowing() && needsRepaint.getAsBoolean()) {
                c.repaint();
            }
        };
        c.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (c.isDisplayable()) {
                    if (!LISTENERS.contains(tick)) {
                        LISTENERS.add(tick);
                    }
                    if (!TIMER.isRunning()) {
                        TIMER.start();
                    }
                } else {
                    LISTENERS.remove(tick);
                    if (LISTENERS.isEmpty()) {
                        TIMER.stop();
                    }
                }
            }
        });
    }
}
