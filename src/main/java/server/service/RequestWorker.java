package server.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import server.controller.ErrorController;
import server.model.HttpExchange;
import server.model.HttpRequest;
import server.model.HttpResponse;
import server.model.HttpStatus;
import server.model.exception.BadRequestException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static java.util.Objects.nonNull;
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
                    .request(HttpRequest.build(lines).orElseThrow(() -> new BadRequestException("Malformed Request. Could not parse HTTP Request!")))
                    .response(HttpResponse.builder()
                            .header("ContentType", "application/json")
                            .build())
                    .user(null)
                    .build();
            // https://stackoverflow.com/questions/13353592/while-reading-from-socket-how-to-detect-when-the-client-is-done-sending-the-requ
            if (exchange.getRequest().getContentLength() > 0) {
                int read;
                StringBuilder sb = new StringBuilder();
                while ((read = br.read()) != -1) {
                    sb.append((char) read);
                    if (sb.length() == exchange.getRequest().getContentLength())
                        break;
                }
                exchange = exchange.toBuilder()
                        .request(exchange.getRequest().toBuilder()
                                .content(sb.toString())
                                .build())
                        .build();
            }
            RequestContext.requestContext.set(exchange);
            sendResponse(requestHandlers.getHandlerOrThrow(exchange));
        } catch (Exception e) {
            sendResponse(ErrorController.getBadRequestError());
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
