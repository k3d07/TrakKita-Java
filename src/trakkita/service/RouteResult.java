package trakkita.service;

import trakkita.model.Reportable;
import trakkita.model.Route;
import trakkita.util.Money;

/** The computed numbers for one route. */
public class RouteResult implements Reportable {
    private final Route route;
    private final double fuelCost;
    private final double gross;
    private final double net;

    public RouteResult(Route route, double fuelCost, double gross, double boundaryFee) {
        this.route = route;
        this.fuelCost = fuelCost;
        this.gross = gross;
        this.net = gross - fuelCost - boundaryFee;
    }

    public Route getRoute() { return route; }
    public double getFuelCost() { return fuelCost; }
    public double getGross() { return gross; }
    public double getNet() { return net; }
    public double getScore() { return fuelCost == 0 ? 0 : net / fuelCost; }

    @Override
    public String summary() {
        return route.getName() + " | net " + Money.peso(net) + " | score " + String.format("%.2f", getScore());
    }
}
