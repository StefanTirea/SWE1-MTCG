package server.model.exception;

public class PathVariableConvertingException extends RuntimeException {

    public PathVariableConvertingException(Throwable cause) {
        super("A Problem occurred converting path variables types", cause);
    }
}
