package http.model.exception;

import http.model.enums.HttpStatus;

public class BadRequestException extends HttpException {

    public BadRequestException(String cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
