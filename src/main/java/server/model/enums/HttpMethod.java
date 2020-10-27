package server.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HttpMethod {

    GET, POST, PUT, DELETE;
}
