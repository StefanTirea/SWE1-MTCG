package http.model.exception;

import http.model.enums.HttpMethod;
import http.model.enums.HttpStatus;

public class MethodNotAllowedException extends HttpException {

    public MethodNotAllowedException(HttpMethod requestMethod, String allowedMethods) {
        super(String.format("This path does not allow %s! Maybe you want to try these: %s", requestMethod, allowedMethods),
                HttpStatus.METHOD_NOT_ALLOWED);
    }
}
