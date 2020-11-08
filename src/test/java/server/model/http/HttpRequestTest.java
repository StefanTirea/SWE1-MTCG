package server.model.http;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import server.model.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static server.fixture.HttpFixture.httpRequest;

@ExtendWith(MockitoExtension.class)
class HttpRequestTest {

    @Test
    @SneakyThrows
    void build_validLines_createRequest() {
        BufferedReader br = spy(new BufferedReader(new StringReader("hello")));
        assertThat(HttpRequest.build(List.of("POST /path HTTP/1.1", "Content-Length: 5", "", "hello"), br))
                .contains(httpRequest(HttpMethod.POST, "/path", "hello"));

        verify(br).read(any(), eq(0), eq("hello".length()));
    }

    @Test
    @SneakyThrows
    void build_emptyLines_empty() {
        BufferedReader br = spy(new BufferedReader(new StringReader("")));
        assertThat(HttpRequest.build(emptyList(), br)).isEmpty();

        verifyNoInteractions(br);
    }

    @Test
    @SneakyThrows
    void build_notValidLines_empty() {
        BufferedReader br = spy(new BufferedReader(new StringReader("hello")));
        assertThat(HttpRequest.build(List.of("dsdsasd dadadsad", "dasdas", "", ""), br)).isEmpty();

        verifyNoInteractions(br);
    }

    @Test
    @SneakyThrows
    void build_validLinesButContentLengthWrong_empty() {
        BufferedReader br = spy(new BufferedReader(new StringReader("hi")));
        assertThat(HttpRequest.build(List.of("POST /path HTTP/1.1", "Content-Length: 5", "", "hello"), br))
                .isEmpty();

        verify(br).read(any(), eq(0), eq("hello".length()));
    }
}
