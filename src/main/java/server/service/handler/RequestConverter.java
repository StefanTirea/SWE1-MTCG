package server.service.handler;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
public class RequestConverter {

    private final Map<Class<?>, Function<String,Object>> pathVariableConverter = new HashMap<>();

    public RequestConverter() {
        registerPathVariableConverter();
    }

    private void registerPathVariableConverter() {
        getPrimitiveTypeConverters();
    }

    private void getPrimitiveTypeConverters() {
        pathVariableConverter.put(int.class, Integer::parseInt);
        pathVariableConverter.put(Integer.class, Integer::parseInt);
        pathVariableConverter.put(String.class, o -> o);
        pathVariableConverter.put(long.class, Long::parseLong);
        pathVariableConverter.put(Long.class, Long::parseLong);
        pathVariableConverter.put(double.class, Double::parseDouble);
        pathVariableConverter.put(Double.class, Double::parseDouble);
        pathVariableConverter.put(Float.class, Float::parseFloat);
        pathVariableConverter.put(float.class, Float::parseFloat);
        pathVariableConverter.put(Boolean.class, Boolean::parseBoolean);
        pathVariableConverter.put(boolean.class, Boolean::parseBoolean);
    }
}
