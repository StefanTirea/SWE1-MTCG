package http.service.handler;

import http.model.exception.BadRequestException;
import http.model.exception.HttpException;
import http.model.exception.HttpRequestParseException;
import http.model.exception.InternalServerErrorException;
import http.model.exception.MethodNotAllowedException;
import http.model.exception.PathVariableConvertingException;
import http.model.exception.UnauthorizedException;
import http.model.http.HttpExchange;
import http.model.http.HttpResponse;
import http.model.http.PathHandler;
import http.model.http.RequestContext;
import http.model.interfaces.Authentication;
import http.service.reflection.ControllerFinder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static http.model.http.RequestContext.HTTP_EXCHANGE_CONTEXT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public class RequestHandler {

    private final List<PathHandler> handlers = new ArrayList<>();
    private final List<Object> controllerObjects = new ArrayList<>();
    private final RequestConverter requestConverter = new RequestConverter();
    private FilterManager filterManager;

    public RequestHandler() {
        ControllerFinder.scanForControllers(this::addRequestHandler, this::addObject, this::instantiateFilterManager);
    }

    public HttpResponse getHandlerOrThrow(HttpExchange exchange) {
        Optional<PathHandler> pathHandler = getPathHandler(exchange);

        return pathHandler
                .map(handler -> {
                    try {
                        Method controllerMethod = handler.getMethod();
                        // find Controller class instance with the Method
                        Object clazzObject = null;
                        for (Object object : controllerObjects) {
                            if (object.getClass().equals(controllerMethod.getDeclaringClass())) {
                                clazzObject = object;
                                break;
                            }
                        }
                        if (clazzObject == null) {
                            throw new IllegalStateException("A method endpoint was called but there was no instance of the controller! " + controllerMethod.getName());
                        }
                        // call the Controller endpoint method with the instance itself and the required parameter
                        return filterManager.handleRequest(handler, clazzObject, getControllerMethodParameters(handler, exchange.getRequestPath(), exchange.getRequestContent()));
                    } catch (PathVariableConvertingException e) {
                        throw new BadRequestException(e);
                    } catch (HttpException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new InternalServerErrorException(e);
                    }
                })
                .orElse(ErrorHandler.getNotFoundError());
    }

    private Optional<PathHandler> getPathHandler(HttpExchange exchange) {
        // Get all possible PathHandlers with the Request Path
        List<PathHandler> possiblePathHandlers = handlers.stream()
                .filter(handler -> exchange.getRequestPath().matches(handler.getRegexPath()))
                .collect(Collectors.toList());
        // Filter by Http Method
        Optional<PathHandler> pathHandler = possiblePathHandlers.stream()
                .filter(handler -> handler.getHttpMethod().equals(exchange.getRequestHttpMethod()))
                .findFirst();
        // throw MethodNotAllowed and create list of possible other HttpMethods
        if (!possiblePathHandlers.isEmpty() && pathHandler.isEmpty()) {
            String allowedMethods = possiblePathHandlers.stream()
                    .map(PathHandler::getHttpMethod)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new MethodNotAllowedException(exchange.getRequestHttpMethod(), allowedMethods);
        }
        return pathHandler;
    }

    private Object[] getControllerMethodParameters(PathHandler pathHandler, String requestPath, String body) throws PathVariableConvertingException {
        Map<String, Object> parameters = convertPathVariables(pathHandler, requestPath);
        if (nonNull(pathHandler.getRequestBodyType())) { // convert body if a RequestBody was expected
            parameters.put(pathHandler.getRequestBodyType().getLeft(), convertRequestBody(pathHandler, body));
        }
        // TODO: Implement System to define injection in Global Config class
        Arrays.stream(pathHandler.getMethod().getParameters())
                .filter(parameter -> HttpExchange.class.equals(parameter.getType()))
                .map(Parameter::getName)
                .findFirst()
                .ifPresent(name -> parameters.put(name, HTTP_EXCHANGE_CONTEXT.get()));

        Arrays.stream(pathHandler.getMethod().getParameters())
                .filter(parameter -> Authentication.class.equals(parameter.getType()))
                .map(Parameter::getName)
                .findFirst()
                .ifPresent(name -> parameters.put(name, HTTP_EXCHANGE_CONTEXT.get().getUser()));

        return Arrays.stream(pathHandler.getMethod().getParameters())
                .map(Parameter::getName)
                .map(parameters::get)
                .toArray();
    }

    private Object convertRequestBody(PathHandler pathHandler, String body) {
        if (isNull(body)) {
            throw new BadRequestException("No body was found in the request!");
        }
        return requestConverter.convertToObject(pathHandler.getRequestBodyType().getRight(), body);
    }

    private Map<String, Object> convertPathVariables(PathHandler pathHandler, String requestPath) throws PathVariableConvertingException {
        try {
            Pattern pattern = Pattern.compile(pathHandler.getRegexPath());
            Matcher matcher = pattern.matcher(requestPath);
            Map<String, Object> pathVariables = new HashMap<>();
            if (matcher.matches()) {
                for (int i = 0; i < pathHandler.getPathVariableOrder().size(); i++) {
                    String pathVariableName = pathHandler.getPathVariableOrder().get(i);
                    Class<?> pathVariableType = pathHandler.getPathVariableTypes().get(pathVariableName);
                    Object pathVariableValue = requestConverter.convertToPrimitive(pathVariableType, matcher.group(i + 1));
                    pathVariables.put(pathVariableName, pathVariableValue);
                }
            }
            return pathVariables;
        } catch (NumberFormatException e) {
            throw new PathVariableConvertingException(e);
        }
    }

    private void addObject(Object object) {
        controllerObjects.add(object);
    }

    /**
     * Adds the pathHandler if the path & httpMethod is not already registered
     *
     * @param pathHandler gets registered
     * @throws IllegalStateException if a duplicate pathHandler (path & httpMethod same) exists
     */
    private void addRequestHandler(PathHandler pathHandler) {
        if (!handlers.contains(pathHandler)) {
            handlers.add(pathHandler);
        } else {
            log.error("PathHandler {} already exists in registered handlers! Duplicate of {}", pathHandler, handlers.get(handlers.indexOf(pathHandler)));
            throw new IllegalStateException("Found PathHandler which !");
        }
    }

    private void instantiateFilterManager(FilterManager filterManager) {
        this.filterManager = filterManager;
    }
}
