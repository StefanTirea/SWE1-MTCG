package server.service;

import lombok.RequiredArgsConstructor;
import server.model.HttpExchange;
import server.model.HttpRequest;
import server.model.HttpResponse;
import server.model.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor
public class RequestWorker implements Runnable {

    private final Socket client;
    private final RequestHandlers requestHandlers;

    @Override
    public void run() {
        try (client;
             BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            List<String> lines = new ArrayList<>();
            String line;
            while (isNotBlank(line = br.readLine())) {
                lines.add(line);
            }
            HttpExchange exchange = HttpExchange.builder()
                    .request(HttpRequest.build(lines).orElseThrow())
                    .response(HttpResponse.builder()
                            .header("ContentType", "application/json")
                            .build())
                    .user(null)
                    .build();
            HttpResponse response = requestHandlers.getHandlerOrThrow(exchange);
            sendResponse(response);
        } catch (Exception e) {
            System.out.println("Invalid Http Request!");
            sendResponse();
        }
    }

    private void sendResponse() {
        try {
            OutputStream clientOutput = client.getOutputStream();
            String response = HttpResponse.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build().toString();
            clientOutput.write(response.getBytes());
            clientOutput.flush();
            clientOutput.close();
        } catch (Exception e) {
        }
    }

    private void sendResponse(HttpResponse httpResponse) {
        try {
            OutputStream clientOutput = client.getOutputStream();
            String response = httpResponse.toString();
            clientOutput.write(response.getBytes());
            clientOutput.flush();
            clientOutput.close();
        } catch (Exception e) {
        }
    }
}
