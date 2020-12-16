package http.model.exception;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(Throwable cause) {
        super("An unexpected exception happened!", cause);
    }
}
