package server.service.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

            processRequestAndRespond(lines, br);
        } catch (IOException e) {
            log.error("Client Exception", e);
        }
    }

    private void processRequestAndRespond(List<String> lines, BufferedReader br) throws IOException {
        try {
            HttpExchange exchange = HttpExchange.builder()
                    .request(HttpRequest.build(lines, br).orElseThrow(BadRequestException::new))
                    .response(HttpResponse.builder()
                            .header("ContentType", "application/json")
                            .build())
                    .user(Optional.empty()) // TODO: Extract User with Header
                    .build();
            log.debug("{}", exchange.getRequest());
            RequestContext.requestContext.set(exchange); // set HttpExchange object in static Thread Context
            sendResponse(requestHandlers.getHandlerOrThrow(exchange));
        } catch (BadRequestException e) {
            log.debug("BadRequestException:", e);
            sendResponse(getBadRequestError(e.getLocalizedMessage()));
        } catch (InternalServerErrorException e) {
            log.error("InternalServerError:", e);
            sendInternalServerError(e);
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
            log.info("The client closed the connection before response could be sent! ({})", client.getInetAddress().getHostAddress());
        }
    }

    private void sendInternalServerError(InternalServerErrorException serverException) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            serverException.getCause().getCause().printStackTrace(pw);
            sendResponse(getInternalServerError(serverException.getLocalizedMessage(), sw.toString()));
        } catch (IOException e) {
            log.error("Could not send InterServerError because of an other Exception!!!", e);
            log.error("Previous InternalServerError: ", serverException);
        }
    }
}
