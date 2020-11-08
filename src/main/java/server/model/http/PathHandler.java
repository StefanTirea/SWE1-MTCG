package server.model.http;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import server.model.enums.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;

@Builder
@Getter
@ToString
public class PathHandler {

    private final String path;
    private final String regexPath;
    private final HttpMethod httpMethod;
    private final Method method;
    private final List<Class<?>> pathVariableTypes;

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
