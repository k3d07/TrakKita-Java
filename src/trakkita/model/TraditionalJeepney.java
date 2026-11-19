package trakkita.model;

/** NOTE: default numbers are placeholders. Replace with the values from your C version. */
public class TraditionalJeepney extends PUV {
    public TraditionalJeepney() { super("Traditional Jeepney"); }

    @Override public double getFuelEfficiencyKmPerL() { return 8.0; }
    @Override public double getDefaultBoundaryFee() { return 500.0; }
    @Override public int getCapacity() { return 22; }

}
