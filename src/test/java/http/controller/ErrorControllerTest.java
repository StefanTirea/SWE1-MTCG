package http.controller;

import org.junit.jupiter.api.Test;
import http.model.enums.HttpMethod;
import http.model.enums.HttpStatus;
import http.model.exception.BadRequestException;
import http.model.exception.InternalServerErrorException;
import http.model.exception.MethodNotAllowedException;
import http.model.http.HttpResponse;

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
