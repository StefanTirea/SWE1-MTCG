package server.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import server.model.http.HttpResponse;
import server.model.enums.HttpStatus;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorController {

    public static HttpResponse getNotFoundError() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    public static HttpResponse getBadRequestError(String message) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .content(Map.of("message", message))
                .build();
    }

    public static HttpResponse getInternalServerError(String message, String exception) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .content(Map.of("message", message, "exception", exception))
                .build();
    }
}
