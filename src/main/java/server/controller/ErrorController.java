package server.controller;

import server.model.HttpResponse;
import server.model.HttpStatus;

import java.util.Map;

public class ErrorController {

    public static HttpResponse getNotFoundError() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    public static HttpResponse getBadRequestError(String message) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .content(Map.of("error", message))
                .build();
    }

    public static HttpResponse getClientClosedRequestError() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.CLIENT_CLOSED_REQUEST)
                .build();
    }
}
