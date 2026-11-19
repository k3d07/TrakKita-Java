package trakkita.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import trakkita.model.Route;

/** Loads and saves route definitions, skipping malformed rows with a warning. */
public final class RouteCsvLoader {
    private RouteCsvLoader() { }

    public static List<Route> load(Path file, List<Route> defaults, List<String> warnings) {
        if (!Files.exists(file)) return new ArrayList<>(defaults);
        List<Route> routes = new ArrayList<>();
        int lineNo = 0;
        try {
            for (String line : Files.readAllLines(file)) {
                lineNo++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
                try {
                    List<String> f = parseCsv(line);
                    if (f.size() != 6) throw new IllegalArgumentException("expected 6 fields");
                    routes.add(new Route(Integer.parseInt(f.get(0).trim()), f.get(1).trim(),
                            Double.parseDouble(f.get(2).trim()), Double.parseDouble(f.get(3).trim()),
                            Double.parseDouble(f.get(4).trim()), Integer.parseInt(f.get(5).trim())));
                } catch (RuntimeException ex) {
                    warnings.add("routes.csv line " + lineNo + " skipped: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            warnings.add("Could not read " + file + ": " + ex.getMessage() + " Using built-in routes.");
            return new ArrayList<>(defaults);
        }
        return routes.isEmpty() ? new ArrayList<>(defaults) : routes;
    }

    public static void save(Path file, List<Route> routes) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        StringBuilder data = new StringBuilder("# id,name,distanceKm,trafficFactor,avgFarePerTrip,tripsPerDay\n");
        for (Route r : routes) {
            data.append(r.getId()).append(',').append(csv(r.getName())).append(',')
                    .append(r.getDistanceKm()).append(',').append(r.getTrafficFactor()).append(',')
                    .append(r.getAvgFarePerTrip()).append(',').append(r.getTripsPerDay()).append('\n');
        }
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        Files.writeString(tmp, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try {
            Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static List<String> parseCsv(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted && ch == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                field.append('"');
                i++;
            } else if (ch == '"') {
                quoted = !quoted;
            } else if (ch == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(ch);
            }
        }
        if (quoted) throw new IllegalArgumentException("unclosed quoted field");
        fields.add(field.toString());
        return fields;
    }
}
