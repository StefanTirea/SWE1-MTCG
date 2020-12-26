package http.model.exception;

import http.model.enums.HttpStatus;

public class UnauthorizedException extends HttpException {

    public UnauthorizedException(String token) {
        super("Could not authenticate Request with token " + token, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        super("No user was not authenticated", HttpStatus.UNAUTHORIZED);
    }
}
