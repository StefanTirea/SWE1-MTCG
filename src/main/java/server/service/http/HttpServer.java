package server.service.http;

import server.service.handler.RequestHandlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private static final int PORT = 8080;
    private RequestHandlers requestHandlers = new RequestHandlers();

    public void run() throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Socket HTTP Server started.\nhttp://localhost:" + PORT);
            while (true) {
                Socket client = server.accept();
                new Thread(new RequestWorker(client, requestHandlers)).start();
            }
        }
    }
}
