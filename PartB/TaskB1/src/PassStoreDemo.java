package assignment5.b1;

import java.nio.file.Files;
import java.nio.file.Path;

public class PassStoreDemo {
    public static void main(String[] args) throws Exception {
        Path file = args.length > 0
                ? Path.of(args[0])
                : Path.of("PartB", "TaskB1", "input", "passes.txt");
        PassStore store = new PassStore(file);

        if (!Files.exists(file)) {
            System.out.println("Missing file test: " + file);
        } else {
            BusPass found = store.find("P1003");
            System.out.println("P1003: " + (found == null ? "null" : found));

            BusPass damaged = store.find("P1002");
            System.out.println("P1002: " + (damaged == null ? "null" : damaged));

            System.out.println("Route R12:");
            for (BusPass pass : store.listByRoute("R12")) {
                System.out.println(pass);
            }
            System.out.println("Damaged records encountered: " + store.getDamagedRecordCount());
        }

        try {
            store.find("P9999");
        } catch (java.io.IOException ex) {
            System.out.println("Missing-file handling: " + ex.getMessage());
        }
    }
}
