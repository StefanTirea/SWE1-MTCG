package server.service.http;

import lombok.RequiredArgsConstructor;
import server.model.enums.HttpStatus;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.http.HttpExchange;
import server.model.http.HttpRequest;
import server.model.http.HttpResponse;
import server.service.RequestContext;
import server.service.handler.RequestHandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static server.controller.ErrorController.getBadRequestError;
import static server.controller.ErrorController.getInternalServerError;

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

            try {
                HttpExchange exchange = HttpExchange.builder()
                        .request(HttpRequest.build(lines, br).orElseThrow(BadRequestException::new))
                        .response(HttpResponse.builder()
                                .httpStatus(HttpStatus.OK)
                                .header("ContentType", "application/json")
                                .build())
                        .user(Optional.empty()) // TODO: Extract User with Header
                        .build();
                RequestContext.requestContext.set(exchange);
                sendResponse(requestHandlers.getHandlerOrThrow(exchange));
            } catch (BadRequestException e) {
                sendResponse(getBadRequestError(e.getLocalizedMessage()));
            } catch (InternalServerErrorException e) {
                sendInternalServerError(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            // TODO: log.info("The client closed the connection before response could be sent! ({})", client.getInetAddress().getHostAddress());
        }
    }

    private void sendInternalServerError(InternalServerErrorException serverException) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            serverException.getCause().getCause().printStackTrace(pw);
            sendResponse(getInternalServerError(serverException.getLocalizedMessage(), sw.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
