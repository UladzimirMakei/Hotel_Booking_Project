package by.uladzimirmakei.hotelbooking.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionCreator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PROPERTIES_PATH
            = "datares\\database.properties";
    private static final String DRIVER_PROPERTY_PATH
            = "db.driver";
    private static final String DATABASE_PROPERTY_PATH
            = "db.url";
    private static final String DATABASE_URL;
    private static final Properties PROPERTIES = new Properties();
    private static ConnectionCreator connectionInstance;

    static {
        try {
            PROPERTIES.load(new FileReader(PROPERTIES_PATH));
            String driverName = (String) PROPERTIES.get(DRIVER_PROPERTY_PATH);
            Class.forName(driverName);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.warn(
                    "Exception is caught in properties of connection creator{}",
                    e.getMessage());
        }
        DATABASE_URL = (String) PROPERTIES.get(DATABASE_PROPERTY_PATH);
    }

    ConnectionCreator() {
    }

    static ConnectionCreator getInstance() {
        if (connectionInstance == null) {
            connectionInstance = new ConnectionCreator();
        }
        return connectionInstance;
    }

    ProxyConnection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(DATABASE_URL, PROPERTIES);
        } catch (SQLException e) {
            LOGGER.warn(
                    "Exception is caught in Connection creation {}",
                    e.getMessage());
        }
        return new ProxyConnection(connection);
    }
}
