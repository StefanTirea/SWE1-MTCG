package http.model.http;

import org.junit.jupiter.api.Test;
import http.model.enums.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static http.fixture.HttpFixture.httpResponse;

class HttpResponseTest {

    @Test
    void getResponseString_httpResponseWithoutHeadersWithContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, "hello").getResponseString())
                .isEqualTo("HTTP/1.1 200 OK\r\n\r\nhello");
    }

    @Test
    void getResponseString_httpResponseWithoutHeadersAndWithoutContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, null).getResponseString())
                .isEqualTo("HTTP/1.1 200 OK\r\n\r\n");
    }

    @Test
    void getResponseString_httpResponseWithHeadersWithContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, "hello").toBuilder()
                .header("test", "value").build().getResponseString())
                .isEqualTo("HTTP/1.1 200 OK\r\ntest: value\r\n\r\nhello");
    }

    @Test
    void getResponseString_httpResponseWithHeadersWithoutContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, null).toBuilder()
                .header("test", "value").build().getResponseString())
                .isEqualTo("HTTP/1.1 200 OK\r\ntest: value\r\n\r\n");
    }

    @Test
    void getResponseString_httpResponseWithJsonContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, List.of("hello", "hi")).getResponseString())
                .isEqualTo("HTTP/1.1 200 OK\r\n\r\n[\"hello\",\"hi\"]");
    }
}
