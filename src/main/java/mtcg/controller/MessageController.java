package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Delete;
import http.model.annotation.Get;
import http.model.annotation.PathVariable;
import http.model.annotation.Post;
import http.model.annotation.Put;
import http.model.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import mtcg.service.MessageService;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Get("/messages")
    public Map<Integer, String> getMessages() {
        return messageService.getMessages();
    }

    @Get("/messages/{id}")
    public Optional<String> getMessage(@PathVariable int id) {
        return messageService.getMessage(id);
    }

    @Post("/messages")
    public Map<String, Integer> createMessage(@RequestBody String message) {
        return Map.of("id", messageService.createMessage(message));
    }

    @Put("/messages/{id}")
    public Optional<String> updateMessage(@PathVariable int id, @RequestBody String message) {
        return messageService.updateMessage(id, message);
    }

    @Delete("/messages/{id}")
    public Optional<String> deleteMessage(@PathVariable int id) {
        return messageService.deleteMessage(id);
    }
}
