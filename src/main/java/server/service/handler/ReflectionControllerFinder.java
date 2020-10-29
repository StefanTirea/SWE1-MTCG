package server.service.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import server.model.enums.HttpMethod;
import server.model.PathHandler;
import server.model.annotation.Controller;
import server.model.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionControllerFinder {

    private static final String PACKAGE_NAME = "server";
    private static final String ANNOTATION_PATH_METHOD_NAME = "value";
    private static final String ANNOTATION_HTTP_METHOD_NAME = "method";

    /**
     * Scans for {@link Controller} annotations in the package to automatically register all REST API endpoints <br>
     * It instantiates all Controller classes and extracts all necessary information to invoke the method when called with a path
     * @param register is called when a {@link PathHandler} is registered
     * @param registerControllerObjects is called for each instantiated class annotated with {@link Controller}
     */
    public static void scanForControllers(Consumer<PathHandler> register, Consumer<Object> registerControllerObjects) {
        Reflections reflections = new Reflections(PACKAGE_NAME);
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);

        controllerClasses.stream()
                .peek(clazz -> {
                    try {
                        registerControllerObjects.accept(clazz.getConstructor().newInstance());
                    } catch (Exception e) {
                        throw new IllegalStateException(String.format("An error occurred when instantiating Controller class %s!", clazz.getName()), e);
                    }
                })
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .forEach(method -> {
                    Pair<String, HttpMethod> requestHandler = Arrays.stream(method.getDeclaredAnnotations())
                            .filter(annotation -> nonNull(annotation.annotationType().getDeclaredAnnotation(RequestMethod.class)))
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
                    if (nonNull(requestHandler)) {
                        method.setAccessible(true);
                        PathHandler pathHandler = PathHandler.builder()
                                .path(requestHandler.getLeft())
                                .regexPath(getRegex(requestHandler.getLeft()))
                                .httpMethod(requestHandler.getRight())
                                .method(method)
                                .pathVariableTypes(Arrays.asList(method.getParameterTypes()))
                                .build();
                        register.accept(pathHandler);
                    }
                });
    }

    /**
     * Example: "/messages/{id}/user" returns "/messages/([^/]+)&#47;user" <br>
     * Creates a regex for HTTP path to match it against an incoming request
     * @param path is a HTTP path
     * @return a regex String
     */
    private static String getRegex(String path) {
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
