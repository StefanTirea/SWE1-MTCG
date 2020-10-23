package server.model;

import lombok.Builder;
import lombok.Data;
import mtcg.model.user.User;

@Builder(toBuilder = true)
@Data
public class HttpExchange {

    private HttpRequest request;
    private HttpResponse response;
    private User user;
}
