package server.model.exception;

import server.model.enums.HttpMethod;

public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException(HttpMethod requestMethod, String allowedMethods) {
        super(String.format("This path does not allow %s! Maybe you want to try these: %s", requestMethod, allowedMethods));
    }
}
