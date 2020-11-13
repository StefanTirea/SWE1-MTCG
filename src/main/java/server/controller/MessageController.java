package server.controller;

import server.model.annotation.Controller;
import server.model.annotation.Delete;
import server.model.annotation.Get;
import server.model.annotation.PathVariable;
import server.model.annotation.Post;
import server.model.annotation.Put;
import server.model.annotation.RequestBody;
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
    public HttpResponse getMessage(@PathVariable int id) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messages.containsKey(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .content(messages.getOrDefault(id, ""))
                .build();
    }

    @Post("/messages")
    public HttpResponse createMessage(@RequestBody String message) {
        int id = counter++;
        messages.put(id, message);
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(Map.of("id", id))
                .build();
    }

    @Put("/messages/{id}")
    public HttpResponse updateMessage(@PathVariable int id, @RequestBody String message) {
        if (messages.containsKey(id)) {
            messages.put(id, message);
        }
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messages.containsKey(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .content(messages.getOrDefault(id, ""))
                .build();
    }

    @Delete("/messages/{id}")
    public HttpResponse deleteMessage(@PathVariable int id) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messages.containsKey(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .content(messages.containsKey(id) ? messages.remove(id) : "")
                .build();
    }

    // TODO: Inject Exchange when necessary (when required in controller parameter)
    // TODO: Allow other types than HttpResponse in controller methods (filter chain required)
    private HttpExchange getHttpExchange() {
        return RequestContext.requestContext.get();
    }
}
