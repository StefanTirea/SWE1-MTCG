package server.controller;

import org.junit.jupiter.api.Test;
import server.model.enums.HttpMethod;
import server.model.enums.HttpStatus;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.exception.MethodNotAllowedException;
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
    void getBadRequestError_BadRequestExceptionWithMessage_returnBadRequestStatus() {
        assertThat(ErrorController.getBadRequestError(new BadRequestException("Request body is missing!")))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .content(Map.of("message", "Request body is missing!"))
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

    @Test
    void getMethodNotAllowedError_returnMethodNotAllowedStatus() {
        assertThat(ErrorController.getMethodNotAllowedError(new MethodNotAllowedException(HttpMethod.GET, "DELETE, POST")))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                        .content(Map.of("message", "This path does not allow GET! Maybe you want to try these: DELETE, POST"))
                        .build());
    }
}
