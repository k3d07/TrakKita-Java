package trakkita.model;

/** NOTE: default numbers are placeholders. Replace with the values from your C version. */
public class Tricycle extends PUV {
    public Tricycle() { super("Tricycle"); }

    @Override public double getFuelEfficiencyKmPerL() { return 25.0; }
    @Override public double getDefaultBoundaryFee() { return 150.0; }
    @Override public int getCapacity() { return 3; }

    @Override public double getDefaultMaintenance() { return 50.0; }
}
