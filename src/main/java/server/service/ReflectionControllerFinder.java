package server.service;

import org.reflections.Reflections;
import server.model.HttpMethod;
import server.model.PathHandler;
import server.model.annotation.Controller;
import server.model.annotation.Delete;
import server.model.annotation.Get;
import server.model.annotation.Post;
import server.model.annotation.Put;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static server.service.RequestHandlers.getRegex;

public class ReflectionControllerFinder {

    public static void scan(Consumer<PathHandler> register, Consumer<Object> registerObjects) {
        Reflections reflections = new Reflections("server");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Controller.class);
        annotated.stream()
                .peek(clazz -> {
                    try {
                        registerObjects.accept(clazz.getConstructor().newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods().clone()))
                .filter(method -> annotations().stream().anyMatch(method::isAnnotationPresent))
                .forEach(method -> {
                    //boolean isHttpMethod = Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotations -> nonNull(annotations.annotationType().getDeclaredAnnotation(server.model.annotation.HttpMethod.class)));
                    String path = "";
                    if (method.isAnnotationPresent(Get.class)) {
                        path = method.getDeclaredAnnotation(Get.class).value();
                    } else if (method.isAnnotationPresent(Post.class)) {
                        path = method.getDeclaredAnnotation(Post.class).value();
                    } else if (method.isAnnotationPresent(Put.class)) {
                        path = method.getDeclaredAnnotation(Put.class).value();
                    } else if (method.isAnnotationPresent(Delete.class)) {
                        path = method.getDeclaredAnnotation(Delete.class).value();
                    }
                    method.setAccessible(true);
                    PathHandler pathHandler = PathHandler.builder()
                            .path(path)
                            .regexPath(getRegex(path))
                            .httpMethod(HttpMethod.GET)
                            .method(method)
                            .pathVariableTypes(Arrays.stream(method.getParameterTypes()).skip(1).collect(Collectors.toList()))
                            .build();
                    register.accept(pathHandler);
                });
    }

    private static List<Class<? extends Annotation>> annotations() {
        return List.of(Get.class, Post.class, Put.class, Delete.class);
    }
}
