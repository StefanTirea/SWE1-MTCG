package server.service.handler;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import server.controller.ErrorController;
import server.model.exception.BadRequestException;
import server.model.exception.InternalServerErrorException;
import server.model.exception.PathVariableConvertingException;
import server.model.http.HttpExchange;
import server.model.http.HttpResponse;
import server.model.http.PathHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

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
                        return (HttpResponse) controllerMethod.invoke(clazzObject, getControllerMethodParameters(handler, exchange.getRequestPath(), exchange.getRequestContent()));
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

    private Object[] getControllerMethodParameters(PathHandler pathHandler, String requestPath, String body) throws PathVariableConvertingException {
        Map<String, Object> parameters = convertPathVariables(pathHandler, requestPath);
        if (nonNull(pathHandler.getRequestBodyType())) {
            Object value = requestConverter.getPathVariableConverter()
                    .getOrDefault(pathHandler.getRequestBodyType().getRight(), content -> new Gson().fromJson(content, pathHandler.getRequestBodyType().getRight()))
                    .apply(body);
            parameters.put(pathHandler.getRequestBodyType().getLeft(), value);
        }
        return Arrays.stream(pathHandler.getMethod().getParameters())
                .map(Parameter::getName)
                .map(parameters::get)
                .toArray();
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
                    Object pathVariableValue = requestConverter.getPathVariableConverter().get(pathVariableType).apply(matcher.group(i + 1));
                    pathVariables.put(pathVariableName, pathVariableValue);
                }
            }
            return pathVariables;
        } catch (NumberFormatException e) {
            throw new PathVariableConvertingException(e);
        }
    }
}
