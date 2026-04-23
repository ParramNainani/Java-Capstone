package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection - Handles database connection using the Singleton pattern
 * and properties file for credentials.
 */
public class DBConnection {
    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = new Properties();
                
                try (InputStream input = new FileInputStream("db.properties")) {
                    props.load(input);
                } catch (IOException e) {
                    System.err.println("Error loading db.properties file: " + e.getMessage());
                    throw new RuntimeException("Could not load database properties.", e);
                }

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String pass = props.getProperty("db.password");
                String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

                try {
                    Class.forName(driver);
                } catch (ClassNotFoundException e) {
                    System.err.println("Database driver class not found: " + e.getMessage());
                    throw new RuntimeException("Could not load database driver.", e);
                }

                connection = DriverManager.getConnection(url, user, pass);
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Failed to establish a database connection.", e);
        }
        
        return connection;
    }
}
