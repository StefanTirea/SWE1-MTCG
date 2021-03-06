package http.service.reflection;

import http.model.annotation.Controller;
import http.model.annotation.PathVariable;
import http.model.annotation.RequestBody;
import http.model.annotation.RequestMethod;
import http.model.annotation.Secured;
import http.model.enums.HttpMethod;
import http.model.http.PathHandler;
import http.service.handler.FilterManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static http.service.reflection.ComponentFinder.scanForComponents;
import static http.service.reflection.FilterFinder.scanForFilters;
import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ControllerFinder {

    private static final String PACKAGE_NAME = "";
    private static final String ANNOTATION_PATH_METHOD_NAME = "value";
    private static final String ANNOTATION_HTTP_METHOD_NAME = "method";

    /**
     * Scans for {@link Controller} annotations in the package to automatically register all REST API endpoints <br>
     * It instantiates all Controller classes and extracts all necessary information to invoke the method when called with a path
     *
     * @param register                  is called when a {@link PathHandler} is registered
     * @param registerControllerObjects is called for each instantiated class annotated with {@link Controller}
     */
    public static void scanForControllers(Map<Class<?>, Object> componentObjects,
                                          Consumer<PathHandler> register,
                                          Consumer<Object> registerControllerObjects) {
        log.info("Controller Finder: Search for Controller classes");
        Reflections reflections = new Reflections(PACKAGE_NAME);
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);

        log.info("Controller Finder: Instantiating Controllers");
        controllerClasses.forEach(clazz -> {
            try {
                registerControllerObjects.accept(componentObjects.get(clazz));
            } catch (Exception e) {
                throw new IllegalStateException(String.format("An error occurred when instantiating Controller class %s!", clazz.getName()), e);
            }
        });

        log.info("Controller Finder: Populating Endpoint Information");
        controllerClasses.stream()
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .forEach(method -> {
                    Pair<String, HttpMethod> requestHandler = mapPathAndHttpMethod(method);
                    if (nonNull(requestHandler)) {
                        register.accept(mapPathHandler(method, requestHandler));
                    }
                });
        log.info("Controller Finder: DONE");
    }

    /**
     * Scans the method for the {@link RequestMethod} Annotation and extracts the Path and HttpMethod
     *
     * @param method located in a Controller class annotated with {@link Controller}
     * @return a Pair with the Path of the endpoint and the HttpMethod found in the Annotation
     */
    private static Pair<String, HttpMethod> mapPathAndHttpMethod(Method method) {
        return Arrays.stream(method.getDeclaredAnnotations())
                .filter(annotation -> nonNull(annotation.annotationType().getDeclaredAnnotation(RequestMethod.class))) // get
                .findFirst()
                .map(annotation -> {
                    try {
                        String value = (String) annotation.annotationType().getDeclaredMethod(ANNOTATION_PATH_METHOD_NAME).invoke(annotation, (Object[]) null);
                        HttpMethod httpMethod = (HttpMethod) annotation.annotationType().getDeclaredMethod(ANNOTATION_HTTP_METHOD_NAME).invoke(annotation, (Object[]) null);
                        return Pair.of(value, httpMethod);
                    } catch (Exception e) {
                        throw new IllegalStateException(String.format("An error occurred when finding the method '%s' or '%s' on %s!",
                                ANNOTATION_PATH_METHOD_NAME, ANNOTATION_HTTP_METHOD_NAME, annotation.getClass().getName()), e);
                    }
                })
                .orElse(null);
    }

    /**
     * Create an instance of {@link PathHandler} to be registered for the Http Server
     *
     * @param method         is the Controller method
     * @param requestHandler is the return value of {@link ControllerFinder#mapPathAndHttpMethod(Method)}
     * @return a {@link PathHandler} with the {@link Method}, path and {@link HttpMethod}
     */
    private static PathHandler mapPathHandler(Method method, Pair<String, HttpMethod> requestHandler) {
        return PathHandler.builder()
                .path(requestHandler.getLeft())
                .regexPath(getRegex(requestHandler.getLeft()))
                .httpMethod(requestHandler.getRight())
                .method(method)
                .pathVariableOrder(mapPathVariableOrder(requestHandler.getLeft()))
                .pathVariableTypes(mapPathVariableTypes(method))
                .requestBodyType(mapRequestBodyType(method))
                .requiredRoles(mapRequiredRoles(method))
                .build();
    }

    private static List<String> mapRequiredRoles(Method method) {
        if (method.isAnnotationPresent(Secured.class)) {
            return Arrays.asList(method.getDeclaredAnnotation(Secured.class).value());
        } else if (method.getDeclaringClass().isAnnotationPresent(Secured.class)) {
            return Arrays.asList(method.getDeclaringClass().getDeclaredAnnotation(Secured.class).value());
        } else {
            return null;
        }
    }

    private static List<String> mapPathVariableOrder(String path) {
        Pattern pattern = Pattern.compile("\\{([A-Za-z0-9]+)\\}");
        Matcher matcher = pattern.matcher(path);
        return matcher.results()
                .map(match -> match.group(1))
                .collect(Collectors.toList());
    }

    private static Map<String, Class<?>> mapPathVariableTypes(Method method) {
        return Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(PathVariable.class))
                .collect(Collectors.toMap(Parameter::getName, Parameter::getType));
    }

    private static Pair<String, Class<?>> mapRequestBodyType(Method method) {
        Map<String, Class<?>> types = Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(RequestBody.class))
                .collect(Collectors.toMap(Parameter::getName, Parameter::getType));
        assert types.size() <= 1;
        return types.entrySet().stream().map(Pair::of).findFirst().orElse(null);
    }

    /**
     * Example: "/messages/{id}/user" returns "^/messages/([^/]+)&#47;user/?$" <br>
     * Creates a regex for HTTP path to match it against an incoming request
     *
     * @param path is a HTTP path
     * @return a regex String
     */
    static String getRegex(String path) {
        List<String> pathVariables = Arrays.stream(path.split("/"))
                .filter(line -> line.startsWith("{"))
                .collect(Collectors.toList());
        String regex = "^" + path.replace("/", "\\/") + "\\/?$";
        for (String variable : pathVariables) {
            regex = regex.replace(variable, "([^/]+)");
        }
        return regex;
    }
}
