package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Delete;
import http.model.annotation.Get;
import http.model.annotation.PathVariable;
import http.model.annotation.Post;
import http.model.annotation.Put;
import http.model.annotation.RequestBody;
import http.model.enums.HttpStatus;
import http.model.http.HttpExchange;
import http.model.http.HttpResponse;
import http.model.http.RequestContext;
import lombok.RequiredArgsConstructor;
import mtcg.service.MessageService;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Get("/messages")
    public HttpResponse getMessages() {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(messageService.getMessages())
                .build();
    }

    @Get("/messages/{id}")
    public HttpResponse getMessage(@PathVariable int id) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messageService.containsMessageId(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .content(messageService.getMessage(id))
                .build();
    }

    @Post("/messages")
    public HttpResponse createMessage(@RequestBody String message) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(HttpStatus.OK)
                .content(Map.of("id", messageService.createMessage(message)))
                .build();
    }

    @Put("/messages/{id}")
    public HttpResponse updateMessage(@PathVariable int id, @RequestBody String message) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messageService.containsMessageId(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .content(messageService.updateMessage(id, message))
                .build();
    }

    @Delete("/messages/{id}")
    public HttpResponse deleteMessage(@PathVariable int id) {
        return getHttpExchange().getResponse().toBuilder()
                .httpStatus(messageService.containsMessageId(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .content(messageService.deleteMessage(id))
                .build();
    }

    // TODO: Inject Exchange when necessary (when required in controller parameter)
    // TODO: Allow other types than HttpResponse in controller methods (filter chain required)
    private HttpExchange getHttpExchange() {
        return RequestContext.requestContext.get();
    }
}
