package server.controller;

import server.model.HttpExchange;
import server.model.HttpResponse;
import server.model.HttpStatus;
import server.model.annotation.Delete;
import server.model.annotation.Get;
import server.model.annotation.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestController {

    private List<String> messages = new ArrayList<>();

    @Get("/")
    public HttpResponse helloWorld(HttpExchange exchange) {
        return exchange.getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(Map.of("hello", "World"))
                .build();
    }

    @Get("/messages")
    public HttpResponse getMessages(HttpExchange exchange) {
        return exchange.getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(messages)
                .build();
    }

    @Post("/messages")
    public HttpResponse createMessage(HttpExchange exchange) {
        messages.add(exchange.getRequest().getContent());
        return exchange.getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(messages.size()-1)
                .build();
    }

    @Delete("/messages/{id}")
    public HttpResponse deleteMessage(HttpExchange exchange) {
        exchange.getRequest().getPathVariables().get(0);
        return exchange.getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
