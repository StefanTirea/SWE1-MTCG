package server.model.http;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.apache.commons.lang3.StringUtils;
import server.model.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Builder(toBuilder = true)
@Getter
@Setter
@EqualsAndHashCode
public class HttpRequest {

    private HttpMethod httpMethod;
    private String path;
    private List<String> pathVariables;
    @Singular
    private Map<String, String> headers;
    private String content;

    public static Optional<HttpRequest> build(List<String> request, BufferedReader br) throws IOException {
        if (request.isEmpty()) {
            return Optional.empty();
        }
        List<String> startLine = Arrays.asList(request.get(0).split(" "));

        HttpMethod httpMethod = HttpMethod.valueOf(startLine.get(0));
        String path = startLine.get(1);

        if (path.length() != 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        Map<String, String> headers = request.stream()
                .skip(1)
                .takeWhile(StringUtils::isNotBlank)
                .map(line -> Arrays.asList(line.split(": ")))
                .filter(header -> header.size() == 2)
                .collect(toMap(header -> header.get(0), header -> header.get(1)));

        String content = readBodyContent(headers, br);

        return Optional.of(HttpRequest.builder()
                .httpMethod(httpMethod)
                .path(path)
                .headers(headers)
                .content(content)
                .build());
    }

    private static String readBodyContent(Map<String, String> headers, BufferedReader br) throws IOException {
        long contentLength = getContentLength(headers);
        if (contentLength > 0) {
            int read;
            StringBuilder sb = new StringBuilder();
            while ((read = br.read()) != -1) {
                sb.append((char) read);
                if (sb.length() == contentLength)
                    break;
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private static long getContentLength(Map<String, String> headers) {
        return Long.parseLong(headers.getOrDefault("Content-Length", "0"));
    }
}
