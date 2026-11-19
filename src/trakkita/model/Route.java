package trakkita.model;

import trakkita.exception.InvalidInputException;
import trakkita.util.Validator;

public class Route {
    private int id;
    private String name;
    private double distanceKm;
    private double trafficFactor;
    private double avgFarePerTrip;
    private int tripsPerDay;

    public Route(int id, String name, double distanceKm, double trafficFactor,
                 double avgFarePerTrip, int tripsPerDay) {
        this.id = id;
        setName(name);
        setDistanceKm(distanceKm);
        setTrafficFactor(trafficFactor);
        setAvgFarePerTrip(avgFarePerTrip);
        setTripsPerDay(tripsPerDay);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getDistanceKm() { return distanceKm; }
    public double getTrafficFactor() { return trafficFactor; }
    public double getAvgFarePerTrip() { return avgFarePerTrip; }
    public int getTripsPerDay() { return tripsPerDay; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Route name is required.");
        }
        this.name = name.trim();
    }
    public void setDistanceKm(double v) { this.distanceKm = Validator.requirePositive(v, "Distance"); }
    public void setTrafficFactor(double v) { this.trafficFactor = Validator.requirePositive(v, "Traffic factor"); }
    public void setAvgFarePerTrip(double v) { this.avgFarePerTrip = Validator.requireNonNegative(v, "Average fare per trip"); }
    public void setTripsPerDay(int v) {
        if (v <= 0) {
            throw new InvalidInputException("Trips per day must be at least 1.");
        }
        this.tripsPerDay = v;
    }
}
