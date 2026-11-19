package trakkita.model;

/** Modern jeepney preset from the original C route recommender. */
public class ModernJeepney extends PUV {
    public ModernJeepney() { super("Modern Jeepney"); }

    @Override public double getFuelEfficiencyKmPerL() { return 1.0 / 0.11; }
    @Override public double getDefaultBoundaryFee() { return 500.0; }
    @Override public int getCapacity() { return 24; }

}
