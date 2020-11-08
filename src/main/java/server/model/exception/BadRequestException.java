package server.model.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("Malformed Request. Could not parse the HTTP Request!");
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
