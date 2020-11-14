package server.model.http;

import org.junit.jupiter.api.Test;
import server.model.enums.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static server.fixture.HttpFixture.httpResponse;

class HttpResponseTest {

    @Test
    void toString_httpResponseWithoutHeadersWithContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, "hello"))
                .hasToString("HTTP/1.1 200 OK\r\n\r\nhello");
    }

    @Test
    void toString_httpResponseWithoutHeadersAndWithoutContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, null).toString())
                .hasToString("HTTP/1.1 200 OK\r\n\r\n");
    }

    @Test
    void toString_httpResponseWithHeadersWithContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, "hello").toBuilder()
                .header("test", "value").build())
                .hasToString("HTTP/1.1 200 OK\r\ntest: value\r\n\r\nhello");
    }

    @Test
    void toString_httpResponseWithHeadersWithoutContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, null).toBuilder()
                .header("test", "value").build())
                .hasToString("HTTP/1.1 200 OK\r\ntest: value\r\n\r\n");
    }

    @Test
    void toString_httpResponseWithJsonContent_properlyFormatted() {
        assertThat(httpResponse(HttpStatus.OK, List.of("hello", "hi")))
                .hasToString("HTTP/1.1 200 OK\r\n\r\n[\"hello\",\"hi\"]");
    }
}
