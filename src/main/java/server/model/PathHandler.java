package server.model;

import lombok.Builder;
import lombok.Data;
import server.model.enums.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;

@Builder
@Data
public class PathHandler {

    private String path;
    private String regexPath;
    private HttpMethod httpMethod;
    private Method method;
    private List<Class<?>> pathVariableTypes;
}
