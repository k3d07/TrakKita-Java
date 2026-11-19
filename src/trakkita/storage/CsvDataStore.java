package trakkita.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import trakkita.exception.DataCorruptedException;
import trakkita.model.DailyEarning;
import trakkita.model.FuelEntry;
import trakkita.model.Persistable;
import trakkita.model.TapSession;

public class CsvDataStore implements DataStore {
    private final Path file;
    private int lastLinesRead;

    public CsvDataStore(String path) {
        this.file = Paths.get(path);
    }

    @Override
    public LoadResult load() throws DataCorruptedException {
        LoadResult result = new LoadResult();
        if (!Files.exists(file)) {
            return result; // first run: nothing to load, not an error
        }
        int lineNo = 0;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    parseInto(line, result);
                } catch (DataCorruptedException | RuntimeException e) {
                    result.getWarnings().add("Line " + lineNo + " skipped: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataCorruptedException("Could not read " + file + ": " + e.getMessage(), lineNo, e);
        } finally {
            lastLinesRead = lineNo;
        }
        return result;
    }

    private void parseInto(String line, LoadResult r) throws DataCorruptedException {
        String[] f = line.split(",");
        if (f.length < 2) {
            throw new DataCorruptedException("not enough fields", 0);
        }
        LocalDate date = LocalDate.parse(f[1].trim());
        switch (f[0].trim()) {
            case "EARNING":
                needFields(f, 5);
                r.getEarnings().add(new DailyEarning(date, num(f[2]), num(f[3]), num(f[4])));
                break;
            case "FUEL":
                needFields(f, 4);
                r.getFuel().add(new FuelEntry(date, num(f[2]), num(f[3])));
                break;
            case "TAP":
                needFields(f, 4);
                r.getSessions().add(new TapSession(date, Integer.parseInt(f[2].trim()), Integer.parseInt(f[3].trim())));
                break;
            default:
                throw new DataCorruptedException("unknown record type '" + f[0] + "'", 0);
        }
    }

    private static void needFields(String[] f, int n) throws DataCorruptedException {
        if (f.length != n) {
            throw new DataCorruptedException("expected " + n + " fields but found " + f.length, 0);
        }
    }

    private static double num(String s) {
        return Double.parseDouble(s.trim());
    }

    @Override
    public void save(List<? extends Persistable> entries) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        try (BufferedWriter bw = Files.newBufferedWriter(tmp)) {
            bw.write("# TrakKita data v1");
            bw.newLine();
            for (Persistable p : entries) {
                bw.write(p.toCsvLine());
                bw.newLine();
            }
        }
        // write-then-rename so a crash mid-save cannot wipe the old file
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
    }

    public int getLastLinesRead() {
        return lastLinesRead;
    }
}
