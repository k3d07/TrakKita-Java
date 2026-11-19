package trakkita.model;

/** Traditional jeepney preset from the original C route recommender. */
public class TraditionalJeepney extends PUV {
    public TraditionalJeepney() { super("Traditional Jeepney"); }

    @Override public double getFuelEfficiencyKmPerL() { return 1.0 / 0.22; }
    @Override public double getDefaultBoundaryFee() { return 500.0; }
    @Override public int getCapacity() { return 22; }

}
