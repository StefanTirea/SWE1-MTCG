package server.model.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import server.model.http.HttpExchange;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestContext {

    public static final ThreadLocal<HttpExchange> requestContext = new ThreadLocal<>();
}
