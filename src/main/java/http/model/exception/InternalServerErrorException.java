package http.model.exception;

import http.model.enums.HttpStatus;

public class InternalServerErrorException extends HttpException {

    public InternalServerErrorException(Throwable cause) {
        super("An unexpected exception happened!", cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
