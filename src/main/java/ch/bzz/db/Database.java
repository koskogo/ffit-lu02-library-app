package ch.bzz.db;

import ch.bzz.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            log.debug("Creating new database connection");
            String url = Config.getProperty("DB_URL");
            String user = Config.getProperty("DB_USER");
            String password = Config.getProperty("DB_PASSWORD");
            
            if (url == null || user == null || password == null) {
                log.error("Database configuration not found. Please check config.properties file.");
                throw new SQLException("Database configuration not found. Please check config.properties file.");
            }
            
            connection = DriverManager.getConnection(url, user, password);
            log.info("Database connection established successfully");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.debug("Database connection closed successfully");
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }
}
