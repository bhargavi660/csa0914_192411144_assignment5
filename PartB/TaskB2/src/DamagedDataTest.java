package assignment5.b2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DamagedDataTest {
    public static void main(String[] args) throws Exception {
        Path file = args.length > 0
                ? Path.of(args[0])
                : Path.of("PartB", "TaskB1", "input", "passes.txt");

        int damaged = 0;
        int valid = 0;
        try (var lines = Files.lines(file, StandardCharsets.UTF_8)) {
            for (String line : (Iterable<String>) lines::iterator) {
                if (line.isBlank()) {
                    damaged++;
                    continue;
                }
                String[] fields = line.split("\\|", -1);
                if (fields.length != 7) {
                    damaged++;
                    continue;
                }
                try {
                    Double.parseDouble(fields[6].trim());
                    valid++;
                } catch (NumberFormatException ex) {
                    damaged++;
                }
            }
        }
        System.out.println("Valid records: " + valid);
        System.out.println("Damaged or skipped records: " + damaged);
    }
}
