package http.model.http;

import http.model.enums.HttpMethod;
import http.model.interfaces.Authentication;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Builder(toBuilder = true)
@Data
public class HttpExchange {

    @NonNull
    private HttpRequest request;
    @NonNull
    private HttpResponse response;
    private Optional<Authentication> user;

    public String getRequestPath() {
        return request.getPath();
    }

    public String getRequestContent() {
        return request.getContent();
    }

    public HttpMethod getRequestHttpMethod() {
        return request.getHttpMethod();
    }
}
