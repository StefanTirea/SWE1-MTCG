package server.model.http;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
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
@ToString
public class HttpRequest {

    private String version;
    private HttpMethod httpMethod;
    private String path;
    @Singular
    private Map<String, String> headers;
    private String content;

    // TODO: Validate Request with Regex
    public static Optional<HttpRequest> build(List<String> request, BufferedReader br) throws IOException {
        if (request.isEmpty()) {
            return Optional.empty();
        }
        List<String> startLine = Arrays.asList(request.get(0).split("\\?")[0].split(" "));

        HttpMethod httpMethod = HttpMethod.valueOf(startLine.get(0));
        String path = startLine.get(1);
        String version = startLine.get(2);

        Map<String, String> headers = mapRequestHeaders(request);

        String content = readBodyContent(headers, br);

        return Optional.of(HttpRequest.builder()
                .version(version)
                .httpMethod(httpMethod)
                .path(path)
                .headers(headers)
                .content(content)
                .build());
    }

    private static Map<String, String> mapRequestHeaders(List<String> request) {
        return request.stream()
                .skip(1)
                .takeWhile(StringUtils::isNotBlank)
                .map(line -> Arrays.asList(line.split(": ")))
                .filter(header -> header.size() == 2)
                .collect(toMap(header -> header.get(0), header -> header.get(1)));
    }

    private static String readBodyContent(Map<String, String> headers, BufferedReader br) throws IOException {
        int contentLength = getContentLength(headers);
        if (contentLength > 0) {
            char[] body = new char[(int) contentLength];
            int charsRead = br.read(body, 0, contentLength);
            if (charsRead != contentLength) {
                throw new IllegalStateException(String.format("Content-Length %d does not equals the read content length %d", contentLength, charsRead));
            }
            return new String(body);
        } else {
            return "";
        }
    }

    private static int getContentLength(Map<String, String> headers) {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }
}
