package by.uladzimirmakei.hotelbooking.service;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
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
    private ConnectionCreator connectionCreator;
    private BlockingQueue<Connection> queue;

    private ConnectionPool() {
        connectionCreator = ConnectionCreator.getInstance();
        this.queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                queue.offer(ConnectionCreator.createConnection());
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
            connection = queue.take();
        } catch (InterruptedException e) {
            // todo
        }
        return connection;
    }

    public void returnConnection(Connection connection) {
        try {
            queue.put(connection);
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

    public void destroyPool() {
//todo
    }
}
