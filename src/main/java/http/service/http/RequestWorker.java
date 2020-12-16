package http.service.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import http.model.exception.BadRequestException;
import http.model.exception.HttpRequestParseException;
import http.model.exception.InternalServerErrorException;
import http.model.exception.MethodNotAllowedException;
import http.model.http.HttpExchange;
import http.model.http.HttpRequest;
import http.model.http.HttpResponse;
import http.model.http.RequestContext;
import http.service.handler.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static http.controller.ErrorController.getBadRequestError;
import static http.controller.ErrorController.getInternalServerError;
import static http.controller.ErrorController.getMethodNotAllowedError;

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
                    .request(HttpRequest.build(lines, br).orElseThrow(HttpRequestParseException::new))
                    .response(HttpResponse.builder()
                            .header("Content-Type", "application/json")
                            .build())
                    .user(Optional.empty()) // TODO: Extract User with Header
                    .build();
            log.debug("{}", exchange.getRequest());

            RequestContext.requestContext.set(exchange); // set HttpExchange object in static Thread Context
            sendResponse(requestHandler.getHandlerOrThrow(exchange));
        } catch (HttpRequestParseException e) {
            BadRequestException exception = new BadRequestException(e);
            log.trace("BadRequestException", exception);
            sendResponse(getBadRequestError(exception));
        } catch (BadRequestException e) {
            log.debug("BadRequestException", e);
            sendResponse(getBadRequestError(e));
        } catch (MethodNotAllowedException e) {
            log.debug("MethodNotAllowedException", e);
            sendResponse(getMethodNotAllowedError(e));
        } catch (InternalServerErrorException e) {
            log.error("InternalServerError", e);
            sendResponse(getInternalServerError(e));
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
            log.warn("The client closed the connection before response could be sent! ({})", client.getInetAddress().getHostAddress());
        }
    }
}
