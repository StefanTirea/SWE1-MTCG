package server.model;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class HttpRequest {

    private final Gson gson = new Gson();
    private HttpVerb httpVerb;
    private String path;
    @Singular
    private Map<String, String> headers;
    private String content;

    public static Optional<HttpRequest> build(List<String> request) {
        if (request.isEmpty()) {
            return Optional.empty();
        }
        List<String> startLine = Arrays.asList(request.get(0).split(" "));

        HttpVerb httpVerb = HttpVerb.valueOf(startLine.get(0));
        String path = startLine.get(1);

        Map<String, String> headers = request.stream()
                .skip(1)
                .takeWhile(StringUtils::isNotBlank)
                .map(line -> Arrays.asList(line.split(": ")))
                .filter(header -> header.size() == 2)
                .collect(toMap(header -> header.get(0), header -> header.get(1)));

        String content = request.stream()
                .dropWhile(StringUtils::isNotBlank)
                .skip(1)
                .collect(Collectors.joining());

        return Optional.of(HttpRequest.builder()
                .httpVerb(httpVerb)
                .path(path)
                .headers(headers)
                .content(content)
                .build());
    }
}
