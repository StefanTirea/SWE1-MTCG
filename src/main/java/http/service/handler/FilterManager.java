package http.service.handler;

import http.model.exception.ForbiddenException;
import http.model.exception.UnauthorizedException;
import http.model.http.HttpResponse;
import http.model.http.PathHandler;
import http.model.interfaces.Authentication;
import http.model.interfaces.Filter;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import mtcg.persistence.base.ConnectionContext;
import mtcg.persistence.base.ConnectionPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

import static http.model.http.RequestContext.HTTP_EXCHANGE_CONTEXT;
import static org.apache.commons.collections4.CollectionUtils.containsAny;

@Builder
@Slf4j
public class FilterManager {

    private final List<Filter> preFilters;
    private final List<Filter> postFilters;
    private final ConnectionPool connectionPool;

    public HttpResponse handleRequest(PathHandler pathHandler,
                                      Object classObject,
                                      Object[] parameters) throws IllegalAccessException, SQLException {
        doFilterBefore();
        checkRoles(pathHandler.getRequiredRoles());
        injectUser(pathHandler.getMethod(), parameters);
        HttpResponse response;
        try {
            response = ResponseConverter.convertToHttpResponse(pathHandler.getMethod().invoke(classObject, parameters), pathHandler.getHttpMethod());
            HTTP_EXCHANGE_CONTEXT.get().setResponse(response);
        } catch (InvocationTargetException e) {
            connectionPool.releaseConnection(ConnectionContext.CONNECTION.get(), false);
            throw new RuntimeException(e.getCause());
        }
        doFilterAfter();
        connectionPool.releaseConnection(ConnectionContext.CONNECTION.get(), true);
        return response;
    }

    private void injectUser(Method method, Object[] parameters) {
        IntStream.range(0, method.getParameterTypes().length)
                .filter(i -> Authentication.class.isAssignableFrom(method.getParameterTypes()[i]))
                .findFirst()
                .ifPresent(i -> parameters[i] = HTTP_EXCHANGE_CONTEXT.get().getUser().orElseThrow()); // TODO why or else throw??
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
