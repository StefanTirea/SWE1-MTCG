package server.fixture;

import server.model.enums.HttpMethod;
import server.model.enums.HttpStatus;
import server.model.http.HttpRequest;
import server.model.http.HttpResponse;

public class HttpFixture {

    public static HttpRequest httpRequest(HttpMethod method, String path, String content) {
        return HttpRequest.builder()
                .version("HTTP/1.1")
                .httpMethod(method)
                .path(path)
                .header("Content-Length", Integer.toString(content.length()))
                .content(content)
                .build();
    }

    public static HttpResponse httpResponse(HttpStatus status, Object content) {
        return HttpResponse.builder()
                .httpStatus(status)
                .content(content)
                .build();
    }
}
