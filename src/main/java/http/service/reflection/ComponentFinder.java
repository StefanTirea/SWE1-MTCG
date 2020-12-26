package http.service.reflection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;
import http.model.annotation.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ComponentFinder {

    @SneakyThrows
    public static Map<Class<?>, Object> scanForComponents(String packageName) {
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

        while (instantiatedClasses.size() != size) {
            componentClasses = typesAnnotatedWith.iterator();
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
        }

        return instantiatedClasses;
    }
}
