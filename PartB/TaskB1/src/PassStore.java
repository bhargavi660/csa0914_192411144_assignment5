package assignment5.b1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PassStore {
    private final Path file;
    private int damagedRecordCount;

    public PassStore(Path file) {
        this.file = file;
    }

    public synchronized void add(BusPass pass) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        String record = pass.toDelimitedString() + System.lineSeparator();
        Files.writeString(file, record, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public BusPass find(String passId) throws IOException {
        if (!Files.exists(file)) {
            throw new IOException("Bus-pass file not found: " + file);
        }

        damagedRecordCount = 0;
        try (java.util.stream.Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            for (String line : (Iterable<String>) lines::iterator) {
                if (line.trim().isEmpty()) {
                    damagedRecordCount++;
                    continue;
                }
                String[] fields = line.split("\\|", -1);
                if (fields.length != 7) {
                    damagedRecordCount++;
                    continue;
                }
                try {
                    double amount = Double.parseDouble(fields[6].trim());
                    if (fields[0].trim().isEmpty()) {
                        damagedRecordCount++;
                        continue;
                    }
                    if (fields[0].trim().equals(passId)) {
                        return new BusPass(fields[0].trim(), fields[1].trim(), fields[2].trim(),
                                fields[3].trim(), fields[4].trim(), fields[5].trim(), amount);
                    }
                } catch (NumberFormatException ex) {
                    damagedRecordCount++;
                }
            }
        }
        return null;
    }

    public List<BusPass> listByRoute(String routeNumber) throws IOException {
        List<BusPass> result = new ArrayList<>();
        if (!Files.exists(file)) {
            throw new IOException("Bus-pass file not found: " + file);
        }
        damagedRecordCount = 0;
        try (java.util.stream.Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            for (String line : (Iterable<String>) lines::iterator) {
                if (line.trim().isEmpty()) {
                    damagedRecordCount++;
                    continue;
                }
                String[] fields = line.split("\\|", -1);
                if (fields.length != 7) {
                    damagedRecordCount++;
                    continue;
                }
                try {
                    double amount = Double.parseDouble(fields[6].trim());
                    if (!fields[0].trim().isEmpty() && fields[3].trim().equals(routeNumber)) {
                        result.add(new BusPass(fields[0].trim(), fields[1].trim(), fields[2].trim(),
                                fields[3].trim(), fields[4].trim(), fields[5].trim(), amount));
                    }
                } catch (NumberFormatException ex) {
                    damagedRecordCount++;
                }
            }
        }
        return result;
    }

    public int getDamagedRecordCount() {
        return damagedRecordCount;
    }
}
