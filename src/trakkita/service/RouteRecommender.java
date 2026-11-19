package trakkita.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import trakkita.model.PUV;
import trakkita.model.Route;
import trakkita.util.Validator;

public class RouteRecommender {
    private final List<Route> routes = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public RouteRecommender() {
        routes.addAll(RouteCsvLoader.load(Path.of("data", "routes.csv"), defaultRoutes(), warnings));
    }

    /** Route seed data copied from the original C route recommender. */
    private static List<Route> defaultRoutes() {
        List<Route> result = new ArrayList<>();
        Object[][] data = {
            {"Legazpi City - Daraga", 14.5, 1.3, 195.0, 8},
            {"Legazpi - Sto. Domingo", 18.0, 1.1, 570.0, 7},
            {"Legazpi - Tabaco City", 32.0, 1.5, 950.0, 4},
            {"Daraga - Camalig", 12.0, 1.2, 250.0, 9},
            {"Legazpi - Ligao City", 28.0, 1.4, 680.0, 5},
            {"Sto. Domingo - Legazpi Coastal", 22.0, 1.0, 540.0, 6},
            {"Legazpi - Rapu-Rapu Ferry+Jeep", 35.0, 1.6, 1160.0, 3},
            {"Daraga - Guinobatan", 20.0, 1.3, 315.0, 6}
        };
        int id = 1;
        for (Object[] r : data) {
            result.add(new Route(id++, (String) r[0], (Double) r[1], (Double) r[2], (Double) r[3], (Integer) r[4]));
        }
        return result;
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    public List<String> getWarnings() { return new ArrayList<>(warnings); }

    public void replaceRoutes(List<Route> updated) {
        if (updated.isEmpty()) {
            throw new IllegalArgumentException("At least one route must remain available.");
        }
        routes.clear();
        routes.addAll(updated);
    }

    public void saveRoutes() throws IOException {
        RouteCsvLoader.save(Path.of("data", "routes.csv"), routes);
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
