package mtcg.persistence.base;

import http.model.annotation.Component;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mtcg.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayDeque;
import java.util.Deque;

@Component
@Slf4j
public final class ConnectionPool {

    private final DatabaseConfig databaseConfig = DatabaseConfig.getConfig();
    private final Deque<Connection> connectionPool = new ArrayDeque<>();
    private int openConnections;

    @SneakyThrows
    protected Connection getConnection() {
        synchronized (connectionPool) {
            if (connectionPool.isEmpty()) {
                if ((openConnections + connectionPool.size()) <= databaseConfig.getPoolSize()) {
                    connectionPool.push(createConnection());
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
            return connectionPool.pop();
        }
    }

    @SneakyThrows
    public void releaseConnection(Connection connection, boolean commit) {
        if (connection.isClosed()) {
            log.warn("Connection should not be closed!");
        } else {
            if (commit) {
                connection.commit();
            } else {
                connection.rollback();
            }
            connectionPool.add(connection);
        }
        openConnections--;
    }

    @SneakyThrows
    private Connection createConnection() {
        Connection connection = DriverManager.getConnection(databaseConfig.getConnectionString(), databaseConfig.getUsername(), databaseConfig.getPassword());
        openConnections++;
        connection.setAutoCommit(false);
        return connection;
    }
}
