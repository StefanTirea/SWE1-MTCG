package http.service.http;

import http.model.exception.BadRequestException;
import http.model.exception.HttpRequestParseException;
import http.model.http.HttpExchange;
import http.model.http.HttpRequest;
import http.model.http.HttpResponse;
import http.model.http.RequestContext;
import http.service.handler.RequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static http.service.handler.ErrorHandler.handleError;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor
@Slf4j
public class RequestWorker implements Runnable {

    private final Socket client;
    private final RequestHandler requestHandler;

    @Override
    public void run() {
        try (client;
             BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            List<String> lines = new ArrayList<>();
            String line;
            while (isNotBlank(line = br.readLine())) {
                lines.add(line);
            }
            processRequestAndRespond(lines, br);
        } catch (IOException e) {
            log.error("Client Exception", e);
        }
    }

    private void processRequestAndRespond(List<String> lines, BufferedReader br) throws IOException {
        try {
            HttpExchange exchange = HttpExchange.builder()
                    .request(HttpRequest.build(lines, br).orElseThrow(() -> new BadRequestException(new HttpRequestParseException())))
                    .response(HttpResponse.builder()
                            .header("Content-Type", "application/json")
                            .build())
                    .user(Optional.empty())
                    .build();
            log.debug("{}", exchange.getRequest());

            RequestContext.HTTP_EXCHANGE_CONTEXT.set(exchange); // set HttpExchange object in static Thread Context
            sendResponse(requestHandler.getHandlerOrThrow(exchange));
        } catch (Exception e) {
            sendResponse(handleError(e));
        }
    }

    private void sendResponse(HttpResponse httpResponse) {
        try {
            OutputStream clientOutput = client.getOutputStream();
            String response = httpResponse.getResponseString();
            clientOutput.write(response.getBytes());
            clientOutput.flush();
            clientOutput.close();
        } catch (IOException e) {
            log.warn("The client closed the connection before response could be sent! ({})", client.getInetAddress().getHostAddress());
        }
    }
}
