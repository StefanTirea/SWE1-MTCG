package http.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import http.model.enums.HttpStatus;
import http.model.exception.BadRequestException;
import http.model.exception.InternalServerErrorException;
import http.model.exception.MethodNotAllowedException;
import http.model.http.HttpResponse;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ErrorController {

    public static HttpResponse getNotFoundError() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    public static HttpResponse getBadRequestError(BadRequestException exception) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .content(mapExceptionCause(exception))
                .build();
    }

    public static HttpResponse getMethodNotAllowedError(MethodNotAllowedException exception) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .content(mapExceptionCause(exception))
                .build();
    }

    public static HttpResponse getInternalServerError(InternalServerErrorException exception) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .content(Map.of("message", exception.getLocalizedMessage(), "exception", exception.getCause().getLocalizedMessage()))
                .build();
    }

    private static Map<String, String> mapExceptionCause(RuntimeException exception) {
        return Map.of("message", exception.getLocalizedMessage());
    }
}
