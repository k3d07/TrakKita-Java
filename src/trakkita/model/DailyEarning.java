package trakkita.model;

import java.time.LocalDate;
import trakkita.exception.InvalidFareException;
import trakkita.util.Money;
import trakkita.util.Validator;

public class DailyEarning extends LogEntry {
    private double totalFares;
    private double fuelCost;
    private double boundaryFee;

    public DailyEarning(LocalDate date, double totalFares, double fuelCost, double boundaryFee) {
        super(date);
        setTotalFares(totalFares);
        setFuelCost(fuelCost);
        setBoundaryFee(boundaryFee);
    }

    public double getTotalFares() { return totalFares; }
    public double getFuelCost() { return fuelCost; }
    public double getBoundaryFee() { return boundaryFee; }

    public void setTotalFares(double v) {
        if (!Double.isFinite(v)) {
            throw new InvalidFareException("Total fares must be a valid number.");
        }
        if (v < 0) {
            throw new InvalidFareException("Total fares cannot be negative.");
        }
        this.totalFares = v;
    }
    public void setFuelCost(double v) { this.fuelCost = Validator.requireNonNegative(v, "Fuel cost"); }
    public void setBoundaryFee(double v) { this.boundaryFee = Validator.requireNonNegative(v, "Boundary fee"); }

    public double getNetProfit() { return totalFares - fuelCost - boundaryFee; }
    public boolean isLoss() { return getNetProfit() < 0; }

    @Override public String getType() { return "EARNING"; }

    @Override protected String fieldsToCsv() { return totalFares + "," + fuelCost + "," + boundaryFee; }

    @Override
    public String summary() {
        return getDate() + " | fares " + Money.peso(totalFares) + " | fuel " + Money.peso(fuelCost)
                + " | boundary " + Money.peso(boundaryFee) + " | net " + Money.peso(getNetProfit());
    }
}
