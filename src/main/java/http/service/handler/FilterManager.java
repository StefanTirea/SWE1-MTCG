package http.service.handler;

import http.model.exception.ForbiddenException;
import http.model.exception.UnauthorizedException;
import http.model.http.HttpResponse;
import http.model.http.PathHandler;
import http.model.interfaces.Authentication;
import http.model.interfaces.Filter;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static http.model.http.RequestContext.HTTP_EXCHANGE_CONTEXT;
import static org.apache.commons.collections4.CollectionUtils.containsAny;

@Builder
@Slf4j
public class FilterManager {

    private final List<Filter> preFilters;
    private final List<Filter> postFilters;

    public HttpResponse handleRequest(PathHandler pathHandler,
                                      Object classObject,
                                      Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        doFilterBefore();
        checkRoles(pathHandler.getRequiredRoles());
        HttpResponse response = ResponseConverter.convertToHttpResponse(pathHandler.getMethod().invoke(classObject, parameters), pathHandler.getHttpMethod());
        HTTP_EXCHANGE_CONTEXT.get().setResponse(response);
        doFilterAfter();

        return response;
    }

    private void checkRoles(List<String> requiredRoles) {
        if (requiredRoles != null) {
            log.debug("Checking if User is authenticated ...");
            Authentication user = HTTP_EXCHANGE_CONTEXT.get().getUser()
                    .orElseThrow(UnauthorizedException::new);
            if (!requiredRoles.isEmpty() && !containsAny(requiredRoles, user.getRoles())) {
                log.debug("User {} does not have required Roles {}", user, requiredRoles);
                throw new ForbiddenException(user.getRoles());
            }
        }
    }

    private void doFilterBefore() {
        preFilters.forEach(filter -> filter.doFilter(HTTP_EXCHANGE_CONTEXT.get()));
    }

    private void doFilterAfter() {
        postFilters.forEach(filter -> filter.doFilter(HTTP_EXCHANGE_CONTEXT.get()));
    }
}
