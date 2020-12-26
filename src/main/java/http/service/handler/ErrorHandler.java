package http.service.handler;

import http.model.enums.HttpStatus;
import http.model.exception.BadRequestException;
import http.model.exception.HttpException;
import http.model.exception.InternalServerErrorException;
import http.model.http.HttpResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ErrorHandler {

    public static HttpResponse handleError(Exception exception) {
        if (exception instanceof BadRequestException) {
            log.trace("BadRequestException", exception);
            return getResponseWithStatus((BadRequestException) exception);
        } else if (exception instanceof InternalServerErrorException) {
            log.error("InternalServerError", exception);
            return getInternalServerError((InternalServerErrorException) exception);
        } else if (exception instanceof HttpException) {
            log.debug(exception.getClass().getSimpleName(), exception);
            return getResponseWithStatus((HttpException) exception);
        } else {
            log.error("Unexpected Exception!", exception);
            return getInternalServerError(new InternalServerErrorException(exception));
        }
    }

    private static HttpResponse getResponseWithStatus(HttpException exception) {
        return HttpResponse.builder()
                .httpStatus(exception.getHttpStatus())
                .content(buildErrorResponse(exception))
                .build();
    }

    public static HttpResponse getNotFoundError() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    private static HttpResponse getInternalServerError(InternalServerErrorException exception) {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .content(Map.of("message", exception.getLocalizedMessage(), "exception", mapExceptionCause(exception)))
                .build();
    }

    private static Map<String, String> buildErrorResponse(HttpException exception) {
        return Map.of("message", exception.getLocalizedMessage());
    }

    private static String mapExceptionCause(InternalServerErrorException exception) {
        return Optional.ofNullable(exception.getCause().getLocalizedMessage())
                .orElse(exception.getCause().getClass().getName());
    }
}
