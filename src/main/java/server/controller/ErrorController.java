package server.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.model.enums.HttpStatus;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.http.HttpResponse;

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
                .content(Map.of("message", exception.getLocalizedMessage()))
                .build();
    }

    public static HttpResponse getInternalServerError(InternalServerErrorException exception) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .content(Map.of("message", exception.getLocalizedMessage(), "exception", exception.getCause().getLocalizedMessage()))
                .build();
    }
}
