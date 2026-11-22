package assignment5.b1;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class PassBenchmark {
    public static void main(String[] args) throws Exception {
        Path root = args.length > 0 ? Path.of(args[0]) : Path.of("PartB", "TaskB1", "measurements");
        Files.createDirectories(root);
        Path report = root.resolve("performance.csv");

        StringBuilder csv = new StringBuilder("test,records_or_mb,method,time_ms" + System.lineSeparator());
        for (int mb : new int[]{1, 10, 50}) {
            Path log = root.resolve("attendance-" + mb + "MB.txt");
            generateLog(log, mb * 1024L * 1024L);
            long unbuffered = countUnbuffered(log);
            long buffered = countBuffered(log);
            csv.append(String.format(Locale.ROOT, "line_count,%d,unbuffered,%d%n", mb, unbuffered));
            csv.append(String.format(Locale.ROOT, "line_count,%d,buffered,%d%n", mb, buffered));
        }

        for (int records : new int[]{1000, 10000, 100000}) {
            Path passes = root.resolve("passes-" + records + ".txt");
            generatePasses(passes, records);
            PassStore store = new PassStore(passes);
            long start = System.nanoTime();
            store.find("P" + (records - 1));
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            csv.append(String.format(Locale.ROOT, "pass_search,%d,scan,%d%n", records, elapsed));
        }
        Files.writeString(report, csv.toString(), StandardCharsets.UTF_8);
        System.out.println("Wrote actual measurements to " + report.toAbsolutePath());
        System.out.print(csv);
    }

    private static void generateLog(Path file, long bytes) throws Exception {
        String line = "192411144|ATTENDANCE|2026-11-22|PRESENT\n";
        try (var out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            long written = 0;
            while (written < bytes) {
                out.write(line);
                written += line.length();
            }
        }
    }

    private static long countBuffered(Path file) throws Exception {
        long start = System.nanoTime();
        long lines = 0;
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            while (reader.readLine() != null) lines++;
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static long countUnbuffered(Path file) throws Exception {
        long start = System.nanoTime();
        long lines = 0;
        try (InputStream in = Files.newInputStream(file)) {
            int value;
            while ((value = in.read()) != -1) {
                if (value == '\n') lines++;
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void generatePasses(Path file, int count) throws Exception {
        try (var out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < count; i++) {
                out.write(String.format(Locale.ROOT,
                        "P%d|19241%06d|Student %d|R%02d|Campus Stop %d|2027-12-31|%.2f%n",
                        i, i, i, i % 20, i % 50, 450.0 + (i % 100)));
            }
        }
    }
}
