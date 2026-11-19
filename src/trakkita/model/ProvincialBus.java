package trakkita.model;

/** NOTE: default numbers are placeholders. Replace with the values from your C version. */
public class ProvincialBus extends PUV {
    public ProvincialBus() { super("Provincial Bus"); }

    @Override public double getFuelEfficiencyKmPerL() { return 3.5; }
    @Override public double getDefaultBoundaryFee() { return 1500.0; }
    @Override public int getCapacity() { return 50; }

    @Override public double getDefaultMaintenance() { return super.getDefaultMaintenance() * 3; }
}
