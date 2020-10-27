package server.service.handler;

import server.controller.ErrorController;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.http.HttpExchange;
import server.model.http.HttpResponse;
import server.model.PathHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        pathHandler.ifPresent(handler -> extractPathVariables(handler.getPath(), exchange));

        return pathHandler
                .map(handler -> {
                    try {
                        Method controllerMethod = handler.getMethod();
                        Object  clazzObject = null;
                        for (Object object : controllerObjects) {
                            if (object.getClass().equals(controllerMethod.getDeclaringClass())) {
                                clazzObject = object;
                                break;
                            }
                        }
                        if (clazzObject == null) {
                            throw new IllegalStateException("A method endpoint was called but there was no instance of the controller! " + controllerMethod.getName());
                        }
                        List<Object> pathVars = new ArrayList<>();
                        for (int i = 0; i < exchange.getRequest().getPathVariables().size(); i++) {
                            Class<?> clazz = handler.getPathVariableTypes().get(i);
                            // Get the right String converter for this type and convert the parsed Path Variable
                            Object converted = requestConverter.getPathVariableConverter().get(clazz)
                                    .apply(exchange.getRequest().getPathVariables().get(i));
                            pathVars.add(converted);
                        }
                        return  (HttpResponse) controllerMethod.invoke(clazzObject, pathVars.toArray());
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

    // TODO: https://stackoverflow.com/questions/1224934/java-extract-strings-with-regex
    private void extractPathVariables(String controllerPath, HttpExchange exchange) {
        String[] path = controllerPath.split("/");
        String[] requestingPath = exchange.getRequestPath().split("/");
        List<String> pathVariables = new ArrayList<>();
        for (int i = 0; i < path.length; i++) {
            if (path[i].startsWith("{")) {
                pathVariables.add(requestingPath[i]);
            }
        }
        exchange.getRequest().setPathVariables(pathVariables);
    }
}
