package trakkita.model;

/** Additional Java preset; its assumptions are not defined by the original C route recommender. */
public class ProvincialBus extends PUV {
    public ProvincialBus() { super("Provincial Bus"); }

    @Override public double getFuelEfficiencyKmPerL() { return 3.5; }
    @Override public double getDefaultBoundaryFee() { return 1500.0; }
    @Override public int getCapacity() { return 50; }

    @Override public double getDefaultMaintenance() { return super.getDefaultMaintenance() * 3; }
}
