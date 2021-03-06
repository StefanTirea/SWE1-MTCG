package http.model.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestContext {

    public static final ThreadLocal<HttpExchange> HTTP_EXCHANGE_CONTEXT = new ThreadLocal<>();
}
