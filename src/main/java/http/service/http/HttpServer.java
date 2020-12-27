package http.service.http;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import http.service.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@NoArgsConstructor
public class HttpServer {

    private int port = 8080;
    private RequestHandler requestHandler = new RequestHandler();
    private ServerSocket server;
    private boolean running;

    public HttpServer(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        running = true;
        server = new ServerSocket(port);
        log.info("Socket HTTP Server started: http://localhost:{}", port);
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
                requestHandler = new RequestHandler();
                log.info("Server stopped");
            } catch (IOException e) { }
        }
    }
}
