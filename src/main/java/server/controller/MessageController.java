package server.controller;

import server.model.annotation.Controller;
import server.model.annotation.Delete;
import server.model.annotation.Get;
import server.model.annotation.Post;
import server.model.annotation.Put;
import server.model.enums.HttpStatus;
import server.model.http.HttpExchange;
import server.model.http.HttpResponse;
import server.service.RequestContext;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MessageController {

    private int counter;
    private final Map<Integer, String> messages = new HashMap<>();

    @Get("/messages")
    public HttpResponse getMessages() {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(messages)
                .build();
    }

    @Get("/messages/{id}")
    public HttpResponse getMessage(int id) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messages.containsKey(id) ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .content(messages.getOrDefault(id, ""))
                .build();
    }

    @Post("/messages")
    public HttpResponse createMessage() {
        messages.put(++counter, getHttpExchange().getRequest().getContent());
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(Map.of("id", counter))
                .build();
    }

    @Put("/messages/{id}")
    public HttpResponse updateMessage(int id) {
        messages.put(id, getHttpExchange().getRequestContent());
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messages.containsKey(id) ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .content(messages.getOrDefault(id, ""))
                .build();
    }

    @Delete("/messages/{id}")
    public HttpResponse deleteMessage(int id) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messages.containsKey(id) ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .content(messages.containsKey(id) ? messages.remove(id) : "")
                .build();
    }

    private HttpExchange getHttpExchange() {
        return RequestContext.requestContext.get();
    }
}
