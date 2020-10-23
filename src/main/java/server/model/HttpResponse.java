package server.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private final Gson gson = new Gson();
    private HttpStatus httpStatus;
    @Singular
    private Map<String, String> headers;
    private Object content;

    @Override
    public String toString() {
        return String.format("%s %s %s\r\n", HTTP_VERSION, httpStatus.getHttpCode(), httpStatus.getHttpStatus()) +
                getHeadersString() +
                (Objects.nonNull(content) ? gson.toJson(content) : "");
    }

    private String getHeadersString() {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n")) + "\r\n\r\n";
    }
}
