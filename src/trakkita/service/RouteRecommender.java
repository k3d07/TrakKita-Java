package trakkita.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import trakkita.model.PUV;
import trakkita.model.Route;
import trakkita.util.Validator;

public class RouteRecommender {
    private final List<Route> routes = new ArrayList<>();

    public RouteRecommender() {
        loadDefaults();
    }

    /** PLACEHOLDER routes. Replace names/numbers with the 8 routes from your C version. */
    private void loadDefaults() {
        Object[][] data = {
            {"Route A", 12.0, 1.2, 250.0, 6}, {"Route B", 8.0, 1.0, 160.0, 9},
            {"Route C", 20.0, 1.4, 420.0, 4}, {"Route D", 15.0, 1.1, 300.0, 5},
            {"Route E", 6.0, 1.3, 110.0, 10}, {"Route F", 25.0, 1.5, 520.0, 3},
            {"Route G", 10.0, 1.0, 210.0, 7}, {"Route H", 18.0, 1.2, 360.0, 5}
        };
        int id = 1;
        for (Object[] r : data) {
            routes.add(new Route(id++, (String) r[0], (Double) r[1], (Double) r[2], (Double) r[3], (Integer) r[4]));
        }
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    /** fuel = km x fuelPerKm x traffic x trips x dieselPrice; net = gross - fuel - boundary. */
    public List<RouteResult> rank(PUV vehicle, double dieselPrice, double boundaryFee) {
        Validator.requirePositive(dieselPrice, "Diesel price");
        Validator.requireNonNegative(boundaryFee, "Boundary fee");
        List<RouteResult> results = new ArrayList<>();
        for (Route r : routes) {
            double fuel = r.getDistanceKm() * vehicle.getFuelPerKm() * r.getTrafficFactor()
                    * r.getTripsPerDay() * dieselPrice;
            double gross = r.getAvgFarePerTrip() * r.getTripsPerDay();
            results.add(new RouteResult(r, fuel, gross, boundaryFee));
        }
        results.sort(Comparator.comparingDouble(RouteResult::getScore).reversed());
        return results;
    }
}
