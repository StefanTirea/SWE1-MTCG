package server.model.http;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import server.model.enums.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@ToString
public class PathHandler {

    private final String path;
    private final String regexPath;
    private final HttpMethod httpMethod;
    private final Method method;
    private final List<String> pathVariableOrder; // chronological order of path variables in path
    private final Map<String, Class<?>> pathVariableTypes; // path variable types of method parameters
    private final Map<String, Class<?>> requestParameterTypes; // request parameter name & value type
    private final Pair<String, Class<?>> requestBodyType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathHandler that = (PathHandler) o;

        if (!regexPath.equals(that.regexPath)) return false;
        return httpMethod == that.httpMethod;
    }

    @Override
    public int hashCode() {
        int result = regexPath.hashCode();
        result = 31 * result + httpMethod.hashCode();
        return result;
    }
}
