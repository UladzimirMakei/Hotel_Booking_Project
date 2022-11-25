package by.uladzimirmakei.hotelbooking.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int POOL_SIZE = 4;
    private static Lock locker = new ReentrantLock();
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static ConnectionPool connectionPool;

    private BlockingQueue<ProxyConnection> connectionQueue;

    private ConnectionPool() {
        ConnectionCreator connectionCreator = ConnectionCreator
                .getInstance();
        this.connectionQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < POOL_SIZE; i++) {
            connectionQueue
                    .offer(connectionCreator.createConnection());
        }
    }

    public static ConnectionPool getInstance() {
        if (!isInitialized.get()) {
            try {
                locker.lock();
                if (connectionPool == null) {
                    connectionPool = new ConnectionPool();
                    isInitialized.set(true);
                }
            } finally {
                locker.unlock();
            }
        }
        return connectionPool;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = connectionQueue.take();
        } catch (InterruptedException e) {
            LOGGER.warn("Exception is caught during getting of connection {}",
                    e.getMessage());
        }
        return connection;
    }

    public void returnConnection(Connection connection) {
        if (connection instanceof ProxyConnection) {
            putConnectionIntoQueue(connection);
        } else {
            LOGGER.info("Returned wrong connection not instance of Proxy");
        }
    }

    public void deregisterAllDrivers() {
        DriverManager.getDrivers().asIterator()
                .forEachRemaining(this::deregisterDriver);
    }

    public void destroyPool() {
        closeAllConnections();
        connectionQueue.clear();
    }

    private void putConnectionIntoQueue(Connection connection) {
        try {
            connectionQueue.put((ProxyConnection) connection);
        } catch (InterruptedException e) {
            LOGGER.warn(
                    "Exception is caught during return of connection {}",
                    e.getMessage());
        }
    }

    private void deregisterDriver(Driver driver) {
        try {
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
            LOGGER.warn(
                    "Exception is caught during deregister of driver {}",
                    e.getMessage());
        }
    }

    private void closeAllConnections() {
        for (int i = 0; i < connectionQueue.size(); i++) {
            takeConnectionFromQueue().reallyClose();
        }
    }

    private ProxyConnection takeConnectionFromQueue() {
        ProxyConnection connection = null;
        try {
            connection = connectionQueue.take();
        } catch (InterruptedException e) {
            LOGGER.warn(
                    "Exception is caught during connection pool destroy {}",
                    e.getMessage());
        }
        return connection;
    }
}
