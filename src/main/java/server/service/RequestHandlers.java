package server.service;

import server.model.HttpExchange;
import server.model.HttpResponse;
import server.model.HttpStatus;
import server.model.HttpVerb;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static server.model.HttpVerb.*;

public class RequestHandlers {

    private final Map<String, Map<HttpVerb, Function<HttpExchange, HttpResponse>>> handlers = new HashMap<>();
    private final TestController testController = new TestController();

    public RequestHandlers() {
        registerHandlers();
    }

    public HttpResponse getHandlerOrThrow(HttpExchange exchange) {
        return handlers.computeIfAbsent(exchange.getRequest().getPath(),
                key -> Map.of(exchange.getRequest().getHttpVerb(), e -> errorNotFound()))
                .get(exchange.getRequest().getHttpVerb())
                .apply(exchange);
    }

    private void registerHandlers() {
        addRequestHandler("/", GET, testController::helloWorld);
    }

    private void addRequestHandler(String path, HttpVerb verb, Function<HttpExchange, HttpResponse> fn) {
        if (!handlers.containsKey(path)) {
            handlers.put(path, new HashMap<>());
        }
        handlers.get(path).put(verb, fn);
    }

    private HttpResponse errorNotFound() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }
}
