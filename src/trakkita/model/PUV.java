package trakkita.model;

/** Abstract parent of all vehicle types. Subclasses supply their own defaults. */
public abstract class PUV {
    private final String name;

    protected PUV(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public abstract double getFuelEfficiencyKmPerL();
    public abstract double getDefaultBoundaryFee();
    public abstract int getCapacity();

    /** Default daily maintenance estimate; subclasses may override. */
    public double getDefaultMaintenance() { return 100.0; }

    public double getFuelPerKm() { return 1.0 / getFuelEfficiencyKmPerL(); }

    @Override
    public String toString() { return name; }
}
