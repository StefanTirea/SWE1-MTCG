package server;

import lombok.SneakyThrows;
import server.service.HttpServer;

public class MTCGServer {

    @SneakyThrows
    public static void main(String[] args) {
        // Start Http Socket Server here
        new HttpServer().run();
    }
}
