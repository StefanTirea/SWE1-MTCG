package server.service.handler;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
public class RequestConverter {

    private final Map<Class<?>, Function<String,Object>> pathVariableConverter = new HashMap<>();

    public RequestConverter() {
        registerPathVariableConverter(pathVariableConverter);
    }

    private void registerPathVariableConverter(Map<Class<?>, Function<String,Object>> converters) {
        getPrimitiveTypeConverters(converters);
    }

    private void getPrimitiveTypeConverters(Map<Class<?>, Function<String,Object>> converters) {
        converters.put(int.class, Integer::parseInt);
        converters.put(Integer.class, Integer::parseInt);
        converters.put(String.class, o -> o);
        converters.put(long.class, Long::parseLong);
        converters.put(Long.class, Long::parseLong);
        converters.put(double.class, Double::parseDouble);
        converters.put(Double.class, Double::parseDouble);
        converters.put(Float.class, Float::parseFloat);
        converters.put(float.class, Float::parseFloat);
        converters.put(Boolean.class, Boolean::parseBoolean);
        converters.put(boolean.class, Boolean::parseBoolean);
    }
}
