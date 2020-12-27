package mtcg;

import http.service.http.HttpServer;
import lombok.SneakyThrows;

public class MTCGServer {

    @SneakyThrows
    public static void main(String[] args) {
        // Start Http Socket Server here
        new HttpServer().run();
    }
}
