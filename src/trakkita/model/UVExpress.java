package trakkita.model;

/** UV Express preset from the original C route recommender. */
public class UVExpress extends PUV {
    public UVExpress() { super("UV Express"); }

    @Override public double getFuelEfficiencyKmPerL() { return 1.0 / 0.08; }
    @Override public double getDefaultBoundaryFee() { return 500.0; }
    @Override public int getCapacity() { return 14; }

}
