package http.service.persistence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionContext {

    public static final ThreadLocal<Connection> CONNECTION = new ThreadLocal<>();
}
