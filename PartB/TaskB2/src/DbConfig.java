package assignment5.b2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConfig {
    private DbConfig() {}

    public static Connection connect() throws SQLException {
        String url = env("DB_URL", "jdbc:mysql://localhost:3306/bus_pass_assignment5");
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
