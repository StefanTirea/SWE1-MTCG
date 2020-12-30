package http.model.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import http.model.enums.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private HttpStatus httpStatus;
    @Singular
    private Map<String, String> headers;
    private Object content;

    public static HttpResponse noContent() {
        return HttpResponse.builder()
                .httpStatus(HttpStatus.NO_CONTENT)
                .build();
    }

    public String getResponseString() {
        return String.format("%s %s %s", HTTP_VERSION, httpStatus.getCode(), httpStatus.getName()) +
                getHeadersString() + getResponseBody();
    }

    private String getHeadersString() {
        String headerString = headers.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
        return (headerString.isBlank() ? "" : "\r\n") + headerString + "\r\n\r\n";
    }

    @SneakyThrows
    private String getResponseBody() {
        if (content instanceof String) {
            return (String) content;
        } else if (nonNull(content)) {
            return new ObjectMapper().writeValueAsString(content);
        } else {
            return "";
        }
    }
}
