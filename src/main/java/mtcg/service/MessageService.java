package mtcg.service;

import http.model.annotation.Component;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class MessageService {

    private int counter;
    private final Map<Integer, String> messages = new ConcurrentHashMap<>();

    public Optional<String> getMessage(int id) {
        return Optional.ofNullable(messages.get(id));
    }

    public int createMessage(String message) {
        messages.put(counter++, message);
        return counter - 1;
    }

    public Optional<String> updateMessage(int id, String message) {
        if (messages.containsKey(id)) {
            messages.put(id, message);
            return Optional.of(message);
        }
        return Optional.empty();
    }

    public Optional<String> deleteMessage(int id) {
        return Optional.ofNullable(messages.remove(id));
    }
}
