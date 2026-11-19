package trakkita.model;

/** Tricycle preset from the original C route recommender. */
public class Tricycle extends PUV {
    public Tricycle() { super("Tricycle"); }

    @Override public double getFuelEfficiencyKmPerL() { return 1.0 / 0.03; }
    @Override public double getDefaultBoundaryFee() { return 500.0; }
    @Override public int getCapacity() { return 3; }

    @Override public double getDefaultMaintenance() { return 50.0; }
}
