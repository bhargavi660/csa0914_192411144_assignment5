package assignment5.b2;

import java.sql.*;

public class LoginDemo {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DbConfig.connect()) {
            System.out.println("Unsafe normal login: " + unsafeLogin(connection, "bhargavi", "pass123"));
            System.out.println("Unsafe invalid login: " + unsafeLogin(connection, "unknown", "wrong"));
            String crafted = "anything' OR '1'='1";
            System.out.println("Unsafe crafted login: " + unsafeLogin(connection, crafted, "anything"));
            System.out.println("Safe normal login: " + safeLogin(connection, "bhargavi", "pass123"));
            System.out.println("Safe invalid login: " + safeLogin(connection, "unknown", "wrong"));
            System.out.println("Safe crafted login: " + safeLogin(connection, crafted, "anything"));
        }
    }

    public static boolean unsafeLogin(Connection connection, String username, String password)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username='" + username
                + "' AND password='" + password + "'";
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getInt(1) > 0;
        }
    }

    public static boolean safeLogin(Connection connection, String username, String password)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username=? AND password=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1) > 0;
            }
        }
    }
}
