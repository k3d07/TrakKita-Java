package trakkita.service;

import trakkita.model.Reportable;
import trakkita.model.Route;
import trakkita.exception.InvalidInputException;
import trakkita.util.Money;
import trakkita.util.Validator;

/** The computed numbers for one route. */
public class RouteResult implements Reportable {
    private final Route route;
    private final double fuelCost;
    private final double gross;
    private final double net;

    public RouteResult(Route route, double fuelCost, double gross, double boundaryFee) {
        this.route = route;
        this.fuelCost = Validator.requireNonNegative(fuelCost, "Route fuel cost");
        this.gross = Validator.requireNonNegative(gross, "Route gross income");
        Validator.requireNonNegative(boundaryFee, "Boundary fee");
        this.net = gross - fuelCost - boundaryFee;
        if (!Double.isFinite(net)) {
            throw new InvalidInputException("Route estimate exceeds the supported numeric range.");
        }
    }

    public Route getRoute() { return route; }
    public double getFuelCost() { return fuelCost; }
    public double getGross() { return gross; }
    public double getNet() { return net; }
    public double getScore() {
        double score = fuelCost == 0 ? 0 : net / fuelCost;
        if (!Double.isFinite(score)) {
            throw new InvalidInputException("Route score exceeds the supported numeric range.");
        }
        return score;
    }

    @Override
    public String summary() {
        return route.getName() + " | net " + Money.peso(net) + " | score " + String.format("%.2f", getScore());
    }
}
