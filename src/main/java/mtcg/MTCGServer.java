package mtcg;

import lombok.SneakyThrows;
import http.service.http.HttpServer;

public class MTCGServer {

    @SneakyThrows
    public static void main(String[] args) {
        // Start Http Socket Server here
        new HttpServer().run();
    }
}
