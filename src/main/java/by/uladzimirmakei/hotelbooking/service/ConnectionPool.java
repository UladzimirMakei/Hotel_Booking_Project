package by.uladzimirmakei.hotelbooking.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static Lock locker = new ReentrantLock();
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static ConnectionPool connectionPool;
    private static final int POOL_SIZE = 4;
    private BlockingQueue<Connection> connectionQueue;

    private ConnectionPool() {
        ConnectionCreator connectionCreator = ConnectionCreator.getInstance();
        this.connectionQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                connectionQueue.offer(ConnectionCreator.createConnection());
            } catch (SQLException e) {
                e.printStackTrace(); //log todo
            }
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
            // todo
        }
        return connection;
    }

    public void returnConnection(Connection connection) {
        try {
            connectionQueue.put(connection);
        } catch (InterruptedException e) {
            //todo
        }
    }

    public void deregisterDriver() {
        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                // todo
            }
        });
    }

    public void destroyPool() throws SQLException {
        for (Connection connection : connectionQueue) {
            connection.close();
        }
        connectionQueue.clear();
    }
}
