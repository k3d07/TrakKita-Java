package trakkita.model;

/** NOTE: default numbers are placeholders. Replace with the values from your C version. */
public class ModernJeepney extends PUV {
    public ModernJeepney() { super("Modern Jeepney"); }

    @Override public double getFuelEfficiencyKmPerL() { return 10.0; }
    @Override public double getDefaultBoundaryFee() { return 700.0; }
    @Override public int getCapacity() { return 24; }

}
