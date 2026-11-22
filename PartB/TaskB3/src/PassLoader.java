package assignment5.b3;

import java.sql.*;
import java.util.Locale;

public class PassLoader {
    private static final String INSERT =
            "INSERT INTO bus_pass(pass_id, register_number, name, route_number, boarding_stop, validity_date, amount_paid) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public static void main(String[] args) throws Exception {
        try (Connection connection = connect()) {
            for (int count : new int[]{10, 1000, 10000, 100000}) {
                truncate(connection);
                long statementTime = insertWithStatement(connection, count);
                truncate(connection);
                long preparedTime = insertWithPrepared(connection, count);
                truncate(connection);
                long batchTime = insertWithBatch(connection, count);
                System.out.printf(Locale.ROOT,
                        "count=%d statement_ms=%d prepared_ms=%d batch_ms=%d%n",
                        count, statementTime, preparedTime, batchTime);
            }
            showMetadata(connection);
            verify(connection);
            testRollback(connection);
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

    private static void truncate(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE bus_pass");
        }
    }

    private static long insertWithStatement(Connection connection, int count) throws SQLException {
        long start = System.nanoTime();
        connection.setAutoCommit(true);
        try (Statement statement = connection.createStatement()) {
            for (int i = 0; i < count; i++) {
                String sql = "INSERT INTO bus_pass VALUES ('P" + i + "','19241" + i
                        + "','Student " + i + "','R" + (i % 20) + "','Stop " + (i % 50)
                        + "','2027-12-31'," + (450.0 + i % 100) + ")";
                statement.executeUpdate(sql);
            }
        }
        return elapsedMs(start);
    }

    private static long insertWithPrepared(Connection connection, int count) throws SQLException {
        long start = System.nanoTime();
        connection.setAutoCommit(true);
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            for (int i = 0; i < count; i++) {
                bind(statement, i);
                statement.executeUpdate();
            }
        }
        return elapsedMs(start);
    }

    private static long insertWithBatch(Connection connection, int count) throws SQLException {
        long start = System.nanoTime();
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            for (int i = 0; i < count; i++) {
                bind(statement, i);
                statement.addBatch();
                if ((i + 1) % 1000 == 0) statement.executeBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
        return elapsedMs(start);
    }

    private static void bind(PreparedStatement statement, int i) throws SQLException {
        statement.setString(1, "P" + i);
        statement.setString(2, "19241" + i);
        statement.setString(3, "Student " + i);
        statement.setString(4, "R" + (i % 20));
        statement.setString(5, "Stop " + (i % 50));
        statement.setDate(6, Date.valueOf("2027-12-31"));
        statement.setDouble(7, 450.0 + i % 100);
    }

    private static long elapsedMs(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }

    static void showMetadata(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT * FROM bus_pass LIMIT 1")) {
            ResultSetMetaData metadata = result.getMetaData();
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                System.out.printf("Column %d: %s (%s)%n",
                        i, metadata.getColumnName(i), metadata.getColumnTypeName(i));
            }
        }
    }

    private static void verify(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) AS total FROM bus_pass")) {
            result.next();
            System.out.println("Verified row count: " + result.getInt("total"));
        }
    }

    private static void testRollback(Connection connection) throws SQLException {
        truncate(connection);
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            for (int i = 0; i < 10; i++) {
                bind(statement, i);
                statement.executeUpdate();
                if (i == 5) throw new SQLException("Controlled failure at record 6");
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            System.out.println("Rollback test: " + ex.getMessage());
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM bus_pass")) {
            result.next();
            System.out.println("Rows after rollback: " + result.getInt(1));
        }
    }
}
