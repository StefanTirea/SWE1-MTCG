package server.controller;

import org.junit.jupiter.api.Test;
import server.model.enums.HttpStatus;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.http.HttpResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorControllerTest {

    @Test
    void getNotFoundError_returnNotFoundStatus() {
        assertThat(ErrorController.getNotFoundError())
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build());
    }

    @Test
    void getBadRequestError_defaultBadRequestException_returnBadRequestStatus() {
        assertThat(ErrorController.getBadRequestError(new BadRequestException()))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .content(Map.of("message", "Malformed Request. Could not parse the HTTP Request!"))
                        .build());
    }

    @Test
    void getBadRequestError_BadRequestExceptionWithOtherException_returnBadRequestStatus() {
        assertThat(ErrorController.getBadRequestError(new BadRequestException(new IllegalArgumentException("Illegal error"))))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .content(Map.of("message", "java.lang.IllegalArgumentException: Illegal error"))
                        .build());
    }

    @Test
    void getInternalServerError_returnInternalErrorStatus() {
        assertThat(ErrorController.getInternalServerError(new InternalServerErrorException(new IllegalArgumentException("Illegal error"))))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .content(Map.of("message", "An unexpected exception happened!", "exception", "Illegal error"))
                        .build());
    }
}
