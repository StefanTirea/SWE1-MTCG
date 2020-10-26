package server.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import server.model.HttpExchange;
import server.model.HttpRequest;
import server.model.HttpResponse;
import server.model.exception.BadRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static server.controller.ErrorController.getBadRequestError;
import static server.controller.ErrorController.getClientClosedRequestError;

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
                    .request(HttpRequest.build(lines).orElseThrow(BadRequestException::new))
                    .response(HttpResponse.builder()
                            .header("ContentType", "application/json")
                            .build())
                    .user(Optional.empty())
                    .build();

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
        } catch (BadRequestException e) {
            sendResponse(getBadRequestError(e.getMessage()));
        } catch (IOException e) {
            sendResponse(getClientClosedRequestError());
        }
    }

    private void sendResponse(HttpResponse httpResponse) {
        try {
            OutputStream clientOutput = client.getOutputStream();
            String response = httpResponse.toString();
            clientOutput.write(response.getBytes());
            clientOutput.flush();
            clientOutput.close();
        } catch (IOException e) {
            //log.info("The client closed the connection before response could be sent! ({})", client.getInetAddress().getHostAddress());
        }
    }
}
