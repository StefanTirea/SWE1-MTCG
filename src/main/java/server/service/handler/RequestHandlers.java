package server.service.handler;

import server.controller.ErrorController;
import server.model.PathHandler;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.http.HttpExchange;
import server.model.http.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class RequestHandlers {

    private final List<PathHandler> handlers = new ArrayList<>();
    private final List<Object> controllerObjects = new ArrayList<>();
    private final RequestConverter requestConverter = new RequestConverter();

    public RequestHandlers() {
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
                        return (HttpResponse) controllerMethod.invoke(clazzObject, getPathVariablesWithRegex(controllerMethod, exchange.getRequestPath(), handler.getRegexPath()));
                    } catch (IllegalAccessException | InvocationTargetException | IllegalStateException e) {
                        throw new InternalServerErrorException(e);
                    } catch (Exception e) {
                        throw new BadRequestException();
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
        }
    }

    private Object[] getPathVariablesWithRegex(Method method, String requestPath, String regexPath) {
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
    }
}
