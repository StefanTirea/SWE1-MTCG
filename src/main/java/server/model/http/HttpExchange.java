package server.model.http;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import mtcg.model.user.User;
import server.model.enums.HttpMethod;

import java.util.Optional;

@Builder(toBuilder = true)
@Data
public class HttpExchange {

    @NonNull
    private HttpRequest request;
    @NonNull
    private HttpResponse response;
    private Optional<User> user;

    public String getRequestPath() {
        return request.getPath();
    }

    public HttpMethod getRequestHttpMethod() {
        return request.getHttpMethod();
    }
}
