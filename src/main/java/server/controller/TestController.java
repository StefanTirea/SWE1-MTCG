package server.controller;

import server.model.HttpExchange;
import server.model.HttpResponse;
import server.model.HttpStatus;
import server.model.annotation.Controller;
import server.model.annotation.Delete;
import server.model.annotation.Get;
import server.model.annotation.Post;
import server.service.RequestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
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

    @Get("/messages/{id}/{s}/{l}/{ll}/{f}")
    public HttpResponse deleteMessage(HttpExchange exchange, int id, String s, Long l, long ll, Float f) {
        return exchange.getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
