package server.controller;

import server.model.http.HttpExchange;
import server.model.http.HttpResponse;
import server.model.enums.HttpStatus;
import server.model.annotation.Controller;
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
    public HttpResponse helloWorld() {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(Map.of("hello", "World"))
                .build();
    }

    @Get("/messages")
    public HttpResponse getMessages() {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(messages)
                .build();
    }

    @Post("/messages")
    public HttpResponse createMessage() {
        messages.add(getHttpExchange().getRequest().getContent());
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(messages.size()-1)
                .build();
    }

    @Get("/messages/{id}/{s}/{l}/{ll}/{f}")
    public HttpResponse deleteMessage(int id, String s, Long l, long ll, Float f) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Get("/test/{name}/test/{id}")
    public HttpResponse deleteMessage(String name, int id) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content("Hey das ist cool " + name)
                .build();
    }

    private HttpExchange getHttpExchange() {
        return RequestContext.requestContext.get();
    }
}
