package server.model.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("Malformed Request. Could not parse HTTP Request!");
    }
}
