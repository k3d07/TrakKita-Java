package trakkita.model;

/** NOTE: default numbers are placeholders. Replace with the values from your C version. */
public class UVExpress extends PUV {
    public UVExpress() { super("UV Express"); }

    @Override public double getFuelEfficiencyKmPerL() { return 11.0; }
    @Override public double getDefaultBoundaryFee() { return 900.0; }
    @Override public int getCapacity() { return 14; }

}
