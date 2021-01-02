package http.service.reflection;

import http.model.interfaces.Filter;
import http.model.interfaces.PostFilter;
import http.model.interfaces.PreFilter;
import http.service.handler.FilterManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import http.service.persistence.ConnectionPool;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class FilterFinder {

    private static final String PACKAGE_NAME = "mtcg";

    @SneakyThrows
    public static FilterManager scanForFilters(Map<Class<?>, Object> componentsObjects) {
        log.info("Filter Chain: Instantiating & Registering Filters");
        List<Class<? extends Filter>> preFilters = new ArrayList<>(new Reflections(PACKAGE_NAME).getSubTypesOf(PreFilter.class));
        List<Class<? extends Filter>> postFilters = new ArrayList<>(new Reflections(PACKAGE_NAME).getSubTypesOf(PostFilter.class));
        List<Filter> instantiatedPreFilters = new ArrayList<>();
        List<Filter> instantiatedPostFilters = new ArrayList<>();

        buildFilters(preFilters, componentsObjects, instantiatedPreFilters);
        buildFilters(postFilters, componentsObjects, instantiatedPostFilters);

        log.info("Filter Chain: DONE");
        return FilterManager.builder()
                .preFilters(instantiatedPreFilters)
                .postFilters(instantiatedPostFilters)
                .connectionPool(new ConnectionPool())
                .build();
    }

    private static void buildFilters(List<Class<? extends Filter>> filters,
                                     Map<Class<?>, Object> componentsObjects,
                                     List<Filter> instantiatedFilters) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        for (Class<?> clazz : filters) {
            if (clazz.getDeclaredConstructors().length != 0) {
                Object[] parameters = Arrays.stream(clazz.getDeclaredConstructors()[0].getParameterTypes())
                        .map(componentsObjects::get)
                        .filter(Objects::nonNull)
                        .toArray();

                if (parameters.length != clazz.getDeclaredConstructors()[0].getParameterTypes().length) {
                    throw new IllegalArgumentException("Could not find objects to instantiate " + clazz.getName());
                }

                instantiatedFilters.add((Filter) clazz.getDeclaredConstructors()[0].newInstance(parameters));
            }
        }
    }
}
