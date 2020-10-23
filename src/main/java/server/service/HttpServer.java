package server.service;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private static final int PORT = 8080;

    public void run() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Socket HTTP Server started on Port 8080.");
            System.out.println("http://localhost:" + PORT);
            while (true) {
                Socket client = server.accept();
                new Thread(new RequestWorker(client)).start();
            }
            //client.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\nHi".getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
