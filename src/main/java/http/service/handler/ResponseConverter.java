package http.service.handler;

import http.model.enums.HttpMethod;
import http.model.enums.HttpStatus;
import http.model.http.HttpResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static http.model.http.RequestContext.HTTP_EXCHANGE_CONTEXT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseConverter {

    public static HttpResponse convertToHttpResponse(Object response, HttpMethod method) {
        HttpResponse context = HTTP_EXCHANGE_CONTEXT.get().getResponse();
        HttpResponse newResponse;
        if (response instanceof HttpResponse) {
            newResponse = (HttpResponse) response;
            if (newResponse.getHttpStatus() == null) {
                newResponse = newResponse.toBuilder()
                        .httpStatus(HttpStatus.OK)
                        .build();
            }
        } else if (response instanceof Optional<?>) {
            newResponse = ((Optional<?>) response)
                    .map(content -> HttpResponse.builder().httpStatus(getStatus(method)).content(content).build())
                    .orElse(HttpResponse.builder().httpStatus(HttpStatus.NOT_FOUND).build());
        } else {
            newResponse = HttpResponse.builder()
                    .httpStatus(getStatus(method))
                    .content(response)
                    .build();
        }

        return newResponse.toBuilder()
                .headers(context.getHeaders())
                .build();
    }

    private static HttpStatus getStatus(HttpMethod method) {
        if (HttpMethod.POST.equals(method)) {
            return HttpStatus.CREATED;
        } else {
            return HttpStatus.OK;
        }
    }
}
