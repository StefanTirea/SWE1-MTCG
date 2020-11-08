package server.service.handler;

import lombok.extern.slf4j.Slf4j;
import server.controller.ErrorController;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.exception.PathVariableConvertingException;
import server.model.http.HttpExchange;
import server.model.http.HttpResponse;
import server.model.http.PathHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
public class RequestHandler {

    private final List<PathHandler> handlers = new ArrayList<>();
    private final List<Object> controllerObjects = new ArrayList<>();
    private final RequestConverter requestConverter = new RequestConverter();

    public RequestHandler() {
        ReflectionControllerFinder.scanForControllers(this::addRequestHandler, this::addObject);
    }

    public HttpResponse getHandlerOrThrow(HttpExchange exchange) {
        Optional<PathHandler> pathHandler = handlers.stream()
                .filter(handler -> exchange.getRequestPath().matches(handler.getRegexPath()))
                .filter(handler -> handler.getHttpMethod().equals(exchange.getRequestHttpMethod()))
                .findFirst();

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
                        return (HttpResponse) controllerMethod.invoke(clazzObject, getPathVariablesWithRegex(controllerMethod, exchange.getRequestPath(), handler.getRegexPath()));
                    } catch (PathVariableConvertingException e) {
                        throw new BadRequestException(e);
                    } catch (Exception e) {
                        throw new InternalServerErrorException(e);
                    }
                })
                .orElse(ErrorController.getNotFoundError());
    }

    private void addObject(Object object) {
        controllerObjects.add(object);
    }

    private void addRequestHandler(PathHandler pathHandler) {
        if (!handlers.contains(pathHandler)) {
            handlers.add(pathHandler);
        } else {
            log.error("PathHandler {} already exists in registered handlers! Duplicate of {}", pathHandler, handlers.get(handlers.indexOf(pathHandler)));
            throw new IllegalStateException("Found PathHandler which !");
        }
    }

    private Object[] getPathVariablesWithRegex(Method method, String requestPath, String regexPath) throws PathVariableConvertingException {
        try {
            Pattern pattern = Pattern.compile(regexPath);
            Matcher matcher = pattern.matcher(requestPath);
            if (matcher.matches()) {
                return IntStream.range(1, method.getParameterTypes().length + 1)
                        .mapToObj(i -> requestConverter.getPathVariableConverter()
                                .get(method.getParameterTypes()[i - 1])
                                .apply(matcher.group(i)))
                        .toArray();
            } else {
                return new Object[0];
            }
        } catch (NumberFormatException e) {
            throw new PathVariableConvertingException(e);
        }
    }
}
