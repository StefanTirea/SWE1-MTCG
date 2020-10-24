package server.controller;

import server.model.HttpResponse;
import server.model.HttpStatus;

public class ErrorController {

    public static HttpResponse getNotFoundError() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    public static HttpResponse getBadRequestError() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }
}
