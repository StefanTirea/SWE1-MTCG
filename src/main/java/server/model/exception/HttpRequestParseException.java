package server.model.exception;

public class HttpRequestParseException extends RuntimeException {

    public HttpRequestParseException() {
        super("Malformed Request. Could not parse the HTTP Request!");
    }
}
