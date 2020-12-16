package http.service.http;

import lombok.extern.slf4j.Slf4j;
import http.service.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class HttpServer {

    private static final int PORT = 8080;
    private final RequestHandler requestHandler = new RequestHandler();
    private ServerSocket server;
    private boolean running;

    public void run() throws IOException {
        running = true;
        server = new ServerSocket(PORT);
        log.info("Socket HTTP Server started: http://localhost:{}", PORT);
        while (running) {
            Socket client = server.accept();
            new Thread(new RequestWorker(client, requestHandler)).start();
        }
    }

    public void stop() {
        running = false;
        if (server != null) {
            try {
                server.close();
                log.info("Server stopped");
            } catch (IOException e) { }
        }
    }
}
