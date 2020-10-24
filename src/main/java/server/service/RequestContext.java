package server.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.model.HttpExchange;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestContext {

    public static ThreadLocal<HttpExchange> requestContext = new ThreadLocal<>();
}
