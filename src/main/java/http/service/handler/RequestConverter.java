package http.service.handler;

import com.google.gson.Gson;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
public class RequestConverter {

    private final Map<Class<?>, Function<String,Object>> stringToObjectConverter = new HashMap<>();

    public RequestConverter() {
        getPrimitiveTypeConverters();
    }

    public Object convertToObject(Class<?> clazz, String content) {
        return stringToObjectConverter.getOrDefault(clazz, s -> new Gson().fromJson(content, clazz))
                .apply(content);
    }

    public Object convertToPrimitive(Class<?> clazz, String content) {
        return stringToObjectConverter.get(clazz).apply(content);
    }

    private void getPrimitiveTypeConverters() {
        stringToObjectConverter.put(int.class, Integer::parseInt);
        stringToObjectConverter.put(Integer.class, Integer::parseInt);
        stringToObjectConverter.put(String.class, o -> o);
        stringToObjectConverter.put(long.class, Long::parseLong);
        stringToObjectConverter.put(Long.class, Long::parseLong);
        stringToObjectConverter.put(double.class, Double::parseDouble);
        stringToObjectConverter.put(Double.class, Double::parseDouble);
        stringToObjectConverter.put(Float.class, Float::parseFloat);
        stringToObjectConverter.put(float.class, Float::parseFloat);
        stringToObjectConverter.put(Boolean.class, Boolean::parseBoolean);
        stringToObjectConverter.put(boolean.class, Boolean::parseBoolean);
    }
}
