package server.model.http;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import server.model.enums.HttpStatus;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private HttpStatus httpStatus;
    @Singular
    private Map<String, String> headers;
    private Object content;

    @Override
    public String toString() {
        return String.format("%s %s %s\r\n", HTTP_VERSION, httpStatus.getHttpCode(), httpStatus.getHttpStatus()) +
                getHeadersString() + getResponseBody();
    }

    private String getHeadersString() {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n")) + "\r\n\r\n";
    }

    private String getResponseBody() {
        if (content instanceof String) {
            if (isNotBlank((String) content)) {
                return (String) content;
            } else {
                return "";
            }
        } else if (nonNull(content)) {
            return new Gson().toJson(content);
        } else {
            return "";
        }
    }

    //TODO: add method for a default response object with defaults headers, ....
}
