package http.service.reflection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.reflections.Reflections;
import http.model.annotation.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ComponentFinder {

    @SneakyThrows
    public static Map<Class<?>, Object> scanForComponents(String packageName) {
        log.info("Dependency Injection: Instantiating Components");
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Component.class);
        int size = typesAnnotatedWith.size();
        Iterator<Class<?>> componentClasses = typesAnnotatedWith.iterator();
        Map<Class<?>, Object> instantiatedClasses = new HashMap<>();

        while (componentClasses.hasNext()) {
            Class<?> clazz = componentClasses.next();
            // remove from possible component class if no constructor is available
            if (clazz.getDeclaredConstructors().length == 0) {
                componentClasses.remove();
                size--;
            } else if (clazz.getDeclaredConstructors()[0].getParameters().length == 0) {
                instantiatedClasses.put(clazz, clazz.getConstructor().newInstance());
                componentClasses.remove();
            }
        }

        int lastSize = instantiatedClasses.size();
        componentClasses = typesAnnotatedWith.iterator();
        while (instantiatedClasses.size() != size) {
            while (componentClasses.hasNext()) {
                Class<?> clazz = componentClasses.next();
                boolean constructionPossible = CollectionUtils.containsAll(instantiatedClasses.keySet(), Arrays.asList(clazz.getDeclaredConstructors()[0].getParameterTypes()));
                if (constructionPossible) {
                    Object[] objects = Arrays.stream(clazz.getDeclaredConstructors()[0].getParameterTypes())
                            .map(instantiatedClasses::get)
                            .toArray();
                    instantiatedClasses.put(clazz, clazz.getDeclaredConstructors()[0].newInstance(objects));
                    componentClasses.remove();
                }
            }
            componentClasses = typesAnnotatedWith.iterator();
            if (lastSize == instantiatedClasses.size()) {
                log.error("Classes uninitialized {}", IteratorUtils.toList(componentClasses));
                throw new IllegalStateException("Could not automatically initialize classes!");
            }
            lastSize = instantiatedClasses.size();
        }
        log.info("Dependency Injection: DONE");
        return instantiatedClasses;
    }
}
