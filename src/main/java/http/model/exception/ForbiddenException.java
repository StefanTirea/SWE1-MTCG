package http.model.exception;

import http.model.enums.HttpStatus;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class ForbiddenException extends HttpException {

    public ForbiddenException(Collection<String> roles) {
        super("Could not access endpoint with roles " + StringUtils.join(roles, ", "), HttpStatus.FORBIDDEN);
    }
}
