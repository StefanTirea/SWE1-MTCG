package http.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Getter
public enum ContentType {

    TEXT_PLAIN("text/plain"),
    APPLICATION_JSON("application/json"),
    UNKNOWN(null),
    NONE(null);

    private final String mimeType;

    public static ContentType getContentTypeByMimeType(String mimeType) {
        if (isNull(mimeType)) {
            return NONE;
        }
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.getMimeType().equals(mimeType))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
