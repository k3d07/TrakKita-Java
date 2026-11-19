package trakkita.service;

import trakkita.exception.InvalidFareException;
import trakkita.exception.InvalidInputException;
import trakkita.util.Validator;

/** Immutable, validated inputs for the Break-Even Simulator. */
public class BreakEvenInput {
    private final double fuelPrice;
    private final double fuelEfficiency;
    private final double routeDistance;
    private final int tripsPerDay;
    private final double boundaryFee;
    private final double maintenance;
    private final double driverWages;
    private final double otherCosts;
    private final double farePerPax;
    private final int paxPerTrip;

    public BreakEvenInput(double fuelPrice, double fuelEfficiency, double routeDistance, int tripsPerDay,
                          double boundaryFee, double maintenance, double driverWages, double otherCosts,
                          double farePerPax, int paxPerTrip) {
        this.fuelPrice = Validator.requirePositive(fuelPrice, "Fuel price");
        this.fuelEfficiency = Validator.requirePositive(fuelEfficiency, "Fuel efficiency");
        this.routeDistance = Validator.requirePositive(routeDistance, "Distance");
        if (tripsPerDay < 1) {
            throw new InvalidInputException("Trips per day must be at least 1.");
        }
        this.tripsPerDay = tripsPerDay;
        this.boundaryFee = Validator.requireNonNegative(boundaryFee, "Boundary fee");
        this.maintenance = Validator.requireNonNegative(maintenance, "Maintenance");
        this.driverWages = Validator.requireNonNegative(driverWages, "Driver wages");
        this.otherCosts = Validator.requireNonNegative(otherCosts, "Other costs");
        if (farePerPax <= 0) {
            throw new InvalidFareException("Fare per passenger must be greater than zero.");
        }
        this.farePerPax = farePerPax;
        if (paxPerTrip < 0) {
            throw new InvalidInputException("Passengers per trip cannot be negative.");
        }
        this.paxPerTrip = paxPerTrip;
    }

    public double getFuelPrice() { return fuelPrice; }
    public double getFuelEfficiency() { return fuelEfficiency; }
    public double getRouteDistance() { return routeDistance; }
    public int getTripsPerDay() { return tripsPerDay; }
    public double getBoundaryFee() { return boundaryFee; }
    public double getMaintenance() { return maintenance; }
    public double getDriverWages() { return driverWages; }
    public double getOtherCosts() { return otherCosts; }
    public double getFarePerPax() { return farePerPax; }
    public int getPaxPerTrip() { return paxPerTrip; }
}
