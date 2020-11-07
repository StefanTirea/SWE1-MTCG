package server.service.http;

import lombok.extern.slf4j.Slf4j;
import server.service.handler.RequestHandlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class HttpServer {

    private static final int PORT = 8080;
    private final RequestHandlers requestHandlers = new RequestHandlers();

    public void run() throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            log.info("Socket HTTP Server started: http://localhost:{}", PORT);
            while (true) {
                Socket client = server.accept();
                new Thread(new RequestWorker(client, requestHandlers)).start();
            }
        }
    }
}
