package http.service.persistence;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
public final class ConnectionPool {

    private final DatabaseConfig databaseConfig = DatabaseConfig.getConfig();
    private final Deque<Connection> pool = new ArrayDeque<>();
    private final List<Connection> allConnections = new ArrayList<>();
    private int openConnections;

    public ConnectionPool() {
        pool.add(createConnection());
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeConnections));
    }

    @SneakyThrows
    public void setConnection() {
        synchronized (pool) {
            if (pool.isEmpty()) {
                if (openConnections <= databaseConfig.getPoolSize()) {
                    pool.push(createConnection());
                } else {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(50);
                        if (i == 49) {
                            log.warn("Request could not get Connection from Connection Pool after 5 seconds!!!");
                            throw new IllegalStateException("Could not process request at the moment. Try again later!");
                        }
                    }
                }
            }
            openConnections++;
            ConnectionContext.CONNECTION.set(pool.pop());
        }
    }

    @SneakyThrows
    public void releaseConnection(boolean commit) {
        Connection connection = ConnectionContext.CONNECTION.get();
        if (connection.isClosed()) {
            log.warn("Connection should not be closed!");
        } else {
            if (commit) {
                connection.commit();
            } else {
                connection.rollback();
            }
            synchronized (pool) {
                pool.add(connection);
            }
        }
        openConnections--;
    }

    @SneakyThrows
    private Connection createConnection() {
        Connection connection = DriverManager.getConnection(databaseConfig.getConnectionString(), databaseConfig.getUsername(), databaseConfig.getPassword());
        allConnections.add(connection);
        connection.setAutoCommit(false);
        return connection;
    }

    @SneakyThrows
    private void closeConnections() {
        for (Connection connection : allConnections) {
            connection.close();
        }
    }
}
