package mtcg.service;

import http.model.annotation.Component;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class MessageService {

    private int counter;
    private final Map<Integer, String> messages = new ConcurrentHashMap<>();

    public boolean containsMessageId(int id) {
        return messages.containsKey(id);
    }

    public String getMessage(int id) {
        return messages.getOrDefault(id, "");
    }

    public int createMessage(String message) {
        messages.put(counter++, message);
        return counter - 1;
    }

    public String updateMessage(int id, String message) {
        if (messages.containsKey(id)) {
            messages.put(id, message);
            return message;
        }
        return "";
    }

    public String deleteMessage(int id) {
        if (messages.containsKey(id)) {
            return messages.remove(id);
        }
        return "";
    }
}
