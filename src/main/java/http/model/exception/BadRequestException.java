package http.model.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String cause) {
        super(cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
