package http.service.http;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpServerITBase {

    private final HttpServer httpServer = new HttpServer();
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void setup() {
        new Thread(() -> {
            try {
                httpServer.run();
            } catch (Exception e) { }
        }).start();
    }

    @AfterEach
    void deconstruct() {
        httpServer.stop();
    }

    @SneakyThrows
    protected HttpResponse<String> sendRequest(HttpRequest request) {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .version(HttpClient.Version.HTTP_1_1);
    }

    protected HttpRequest.BodyPublisher createContent(String content) {
        return HttpRequest.BodyPublishers.ofString(content);
    }
}
