package http.service.http;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

/**
 * A very WIP way to Integration Test the Controllers (Don't judge me O.o)
 * => Creates a new Context for every Test (approx. 800ms to recreate Dependency Injection, ...)
 */
public abstract class HttpServerITBase {

    private static final int PORT = new Random().nextInt(2000) + 9000;
    private static final HttpServer httpServer = new HttpServer(PORT);
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void setup() {
        new Thread(() -> {
            try {
                httpServer.run();
            } catch (Exception e) { }
        }).start();
    }

    @SneakyThrows
    @AfterEach
    void deconstruct() {
        httpServer.stop();
    }

    @SneakyThrows
    protected HttpResponse<String> sendRequest(HttpRequest request) {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpRequest.Builder request(String path, String token) {
        return HttpRequest.newBuilder()
                .header("Authorization", "Basic " + token)
                .uri(URI.create("http://localhost:" + PORT + path))
                .version(HttpClient.Version.HTTP_1_1);
    }

    protected HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + path))
                .version(HttpClient.Version.HTTP_1_1);
    }

    protected HttpRequest.BodyPublisher createContent(String content) {
        return HttpRequest.BodyPublishers.ofString(content);
    }
}
