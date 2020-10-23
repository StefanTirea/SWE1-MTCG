package server.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HttpVerb {

    GET, POST, PUT, DELETE;
}
