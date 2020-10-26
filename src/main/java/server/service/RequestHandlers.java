package server.service;

import org.apache.commons.lang3.ArrayUtils;
import server.controller.ErrorController;
import server.model.HttpExchange;
import server.model.HttpResponse;
import server.model.PathHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestHandlers {

    private final List<PathHandler> handlers = new ArrayList<>();
    private final List<Object> controllerObjects = new ArrayList();
    private final HashMap<Class<?>, Function<String,Object>> converters = new HashMap();

    public RequestHandlers() {
        ReflectionControllerFinder.scan(this::addRequestHandler, this::addObject);
        converters.put(int.class, Integer::parseInt);
        converters.put(Integer.class, Integer::parseInt);
        converters.put(String.class, o -> o);
        converters.put(long.class, Long::parseLong);
        converters.put(Long.class, Long::parseLong);
        converters.put(Double.class, Double::parseDouble);
        converters.put(Double.class, Double::parseDouble);
        converters.put(Float.class, Float::parseFloat);
        converters.put(float.class, Float::parseFloat);
        converters.put(Boolean.class, Boolean::parseBoolean);
        converters.put(boolean.class, Boolean::parseBoolean);
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
                            return null;
                        }
                        List<Object> pathVars = new ArrayList<>();
                        for (int i = 0; i < exchange.getRequest().getPathVariables().size(); i++) {
                            Class<?> clazz = handler.getPathVariableTypes().get(i);
                            Object converted = converters.get(clazz).apply(exchange.getRequest().getPathVariables().get(i));
                            pathVars.add(converted);
                        }

                        return  (HttpResponse) controllerMethod.invoke(clazzObject, ArrayUtils.addAll(List.of(exchange).toArray(), pathVars.toArray()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
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

    public static String getRegex(String path) {
        List<String> pathVariables = Arrays.stream(path.split("/"))
                .filter(line -> line.startsWith("{"))
                .collect(Collectors.toList());
        String regex = "^" + path.replace("/", "\\/") + "$";
        for (String variable : pathVariables) {
            regex = regex.replace(variable, "[^/]*");
        }
        return regex;
    }
}
