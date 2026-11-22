package assignment5.b4;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class MigrationProgram {
    private static final String INSERT =
            "INSERT INTO bus_pass(pass_id, register_number, name, route_number, boarding_stop, validity_date, amount_paid) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public static void main(String[] args) throws Exception {
        Path input = args.length > 0
                ? Path.of(args[0])
                : Path.of("PartB", "TaskB1", "input", "passes.txt");
        try (Connection connection = connect()) {
            reportDatabaseMetadata(connection);
            migrate(connection, input);
        }
    }

    static Connection connect() throws SQLException {
        String url = env("DB_URL", "jdbc:mysql://localhost:3306/bus_pass_assignment5");
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static void migrate(Connection connection, Path input) throws Exception {
        int successful = 0;
        int failed = 0;
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(INSERT);
             var lines = Files.lines(input, StandardCharsets.UTF_8)) {
            for (String line : (Iterable<String>) lines::iterator) {
                try {
                    if (line.isBlank()) {
                        failed++;
                        continue;
                    }
                    String[] fields = line.split("\\|", -1);
                    if (fields.length != 7 || fields[0].isBlank()) {
                        failed++;
                        continue;
                    }
                    double amount = Double.parseDouble(fields[6].trim());
                    statement.setString(1, fields[0].trim());
                    statement.setString(2, fields[1].trim());
                    statement.setString(3, fields[2].trim());
                    statement.setString(4, fields[3].trim());
                    statement.setString(5, fields[4].trim());
                    statement.setDate(6, Date.valueOf(fields[5].trim()));
                    statement.setDouble(7, amount);
                    statement.addBatch();
                    successful++;
                    if (successful % 1000 == 0) statement.executeBatch();
                } catch (RuntimeException | SQLException ex) {
                    failed++;
                }
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            successful = 0;
            throw ex;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
        System.out.println("Migration successful records: " + successful);
        System.out.println("Migration failed/invalid records: " + failed);
    }

    static void reportDatabaseMetadata(Connection connection) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        System.out.println("Database product: " + metadata.getDatabaseProductName());
        System.out.println("Database version: " + metadata.getDatabaseProductVersion());
        System.out.println("JDBC driver: " + metadata.getDriverName());
        System.out.println("JDBC driver version: " + metadata.getDriverVersion());
        System.out.println("Available tables:");
        try (ResultSet tables = metadata.getTables(connection.getCatalog(), null, "%",
                new String[]{"TABLE"})) {
            while (tables.next()) {
                System.out.println("- " + tables.getString("TABLE_NAME"));
            }
        }
    }
}
