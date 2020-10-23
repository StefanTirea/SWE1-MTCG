package server.service;

import server.model.HttpExchange;
import server.model.HttpResponse;
import server.model.HttpStatus;

import java.util.Map;

public class TestController {

    public HttpResponse helloWorld(HttpExchange exchange) {
        return exchange.getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(Map.of("hello", "World"))
                .build();
    }
}
