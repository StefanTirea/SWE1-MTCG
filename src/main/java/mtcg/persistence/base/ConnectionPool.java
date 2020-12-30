package mtcg.persistence.base;

import http.model.annotation.Component;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mtcg.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Stack;

@Component
@Slf4j
public final class ConnectionPool {

    private final DatabaseConfig databaseConfig = DatabaseConfig.getConfig();
    private final Stack<Connection> connectionPool = new Stack<>();
    private int openConnections;

    @SneakyThrows
    protected Connection getConnection() {
        synchronized (connectionPool) {
            if (connectionPool.isEmpty()) {
                if (openConnections <= databaseConfig.getPoolSize()) {
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
    protected void releaseConnection(Connection connection) {
        if (connection.isClosed()) {
            log.warn("Connection should not be closed!");
        }
        // TODO when exception rollback OR when sucessfull commmit! Maybe with threadlocal!
        connection.commit();
        connectionPool.add(connection);
    }

    @SneakyThrows
    private Connection createConnection() {
        Connection connection = DriverManager.getConnection(databaseConfig.getConnectionString(), databaseConfig.getUsername(), databaseConfig.getPassword());
        connection.setAutoCommit(false);
        return connection;
    }
}
