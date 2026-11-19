package trakkita.service;

public class BreakEvenResult {
    private final double fuelCost;
    private final double totalCost;
    private final int totalPassengers;
    private final double grossRevenue;
    private final double netProfit;
    private final int breakEvenPax;
    private final int breakEvenPerTrip;
    private final double coveragePct;

    public BreakEvenResult(double fuelCost, double totalCost, int totalPassengers, double grossRevenue,
                           double netProfit, int breakEvenPax, int breakEvenPerTrip, double coveragePct) {
        this.fuelCost = fuelCost;
        this.totalCost = totalCost;
        this.totalPassengers = totalPassengers;
        this.grossRevenue = grossRevenue;
        this.netProfit = netProfit;
        this.breakEvenPax = breakEvenPax;
        this.breakEvenPerTrip = breakEvenPerTrip;
        this.coveragePct = coveragePct;
    }

    public double getFuelCost() { return fuelCost; }
    public double getTotalCost() { return totalCost; }
    public int getTotalPassengers() { return totalPassengers; }
    public double getGrossRevenue() { return grossRevenue; }
    public double getNetProfit() { return netProfit; }
    public int getBreakEvenPax() { return breakEvenPax; }
    public int getBreakEvenPerTrip() { return breakEvenPerTrip; }
    public double getCoveragePct() { return coveragePct; }
    public boolean isLoss() { return netProfit < 0; }
    public int getShortfallPax() { return Math.max(0, breakEvenPax - totalPassengers); }
}
