package http.service.handler;

import http.model.enums.HttpMethod;
import http.model.enums.HttpStatus;
import http.model.exception.BadRequestException;
import http.model.exception.InternalServerErrorException;
import http.model.exception.MethodNotAllowedException;
import http.model.http.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    @Test
    void getNotFoundError_returnNotFoundStatus() {
        assertThat(ErrorHandler.getNotFoundError())
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .build());
    }

    @Test
    void getBadRequestError_BadRequestExceptionWithMessage_returnBadRequestStatus() {
        assertThat(ErrorHandler.handleError(new BadRequestException("Request body is missing!")))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .content(Map.of("message", "Request body is missing!"))
                        .build());
    }

    @Test
    void getBadRequestError_BadRequestExceptionWithOtherException_returnBadRequestStatus() {
        assertThat(ErrorHandler.handleError(new BadRequestException(new IllegalArgumentException("Illegal error"))))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .content(Map.of("message", "java.lang.IllegalArgumentException: Illegal error"))
                        .build());
    }

    @Test
    void getInternalServerError_returnInternalErrorStatus() {
        assertThat(ErrorHandler.handleError(new InternalServerErrorException(new IllegalArgumentException("Illegal error"))))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .content(Map.of("message", "An unexpected exception happened!", "exception", "Illegal error"))
                        .build());
    }

    @Test
    void getMethodNotAllowedError_returnMethodNotAllowedStatus() {
        assertThat(ErrorHandler.handleError(new MethodNotAllowedException(HttpMethod.GET, "DELETE, POST")))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                        .content(Map.of("message", "This path does not allow GET! Maybe you want to try these: DELETE, POST"))
                        .build());
    }

    @Test
    void handleError_unexpectedExceptionHappened_handlesCorrectly() {
        assertThat(ErrorHandler.handleError(new IllegalStateException()))
                .isEqualTo(HttpResponse.builder()
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .content(Map.of("message", "An unexpected exception happened!", "exception", "java.lang.IllegalStateException"))
                        .build());
    }
}
