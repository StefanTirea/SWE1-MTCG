package mtcg.controller;

import http.service.http.HttpServerITBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static http.model.enums.HttpStatus.CREATED;
import static http.model.enums.HttpStatus.NOT_FOUND;
import static http.model.enums.HttpStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ControllerIT works only by starting the PostgreSQL Container by yourself
 * => Disable for Github Actions but can be run by the Dev
 * => Testcontainers could be used when a better configuration file system is implemented
 */
@Disabled
class MessageControllerIT extends HttpServerITBase {

    @Test
    void getMessages_emptyMessages_OK() {
        HttpResponse<String> response = getMessages();

        assertThat(response.body()).isEqualTo("{}");
        assertThat(response.statusCode()).isEqualTo(OK.getCode());
    }

    @Test
    void getMessages_twoMessages_OK() {
        createMessage("message");
        createMessage("message2");

        HttpResponse<String> response = getMessages();

        assertThat(response.body()).isEqualTo("{\"0\":\"message\",\"1\":\"message2\"}");
        assertThat(response.statusCode()).isEqualTo(OK.getCode());
    }

    @Test
    void getMessage_noMessage_NotFound() {
        HttpResponse<String> response = getMessage(0);

        assertThat(response.body()).isEmpty();
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.getCode());
    }

    @Test
    void getMessage_messageAvailable_OK() {
        createMessage("message");

        HttpResponse<String> response = getMessage(0);

        assertThat(response.body()).isEqualTo("message");
        assertThat(response.statusCode()).isEqualTo(OK.getCode());
    }

    @Test
    void createMessage_returnId_OK() {
        HttpResponse<String> before = getMessages();
        HttpResponse<String> response = createMessage("Hello World!");
        HttpResponse<String> after = getMessages();

        assertThat(before.body()).isEqualTo("{}");
        assertThat(response.body()).isEqualTo("{\"id\":0}");
        assertThat(response.statusCode()).isEqualTo(CREATED.getCode());
        assertThat(after.body()).isEqualTo("{\"0\":\"Hello World!\"}");
    }

    @Test
    void updateMessage_invalidId_NotFound() {
        HttpResponse<String> response = sendRequest(request("/messages/0")
                .PUT(createContent("message"))
                .build());

        assertThat(response.body()).isEmpty();
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.getCode());
    }

    @Test
    void updateMessage_replaceMessage_OK() {
        createMessage("message");

        HttpResponse<String> response = sendRequest(request("/messages/0")
                .PUT(createContent("new message"))
                .build());

        assertThat(response.body()).isEqualTo("new message");
        assertThat(response.statusCode()).isEqualTo(OK.getCode());
        assertThat(getMessages().body()).isEqualTo("{\"0\":\"new message\"}");
    }

    @Test
    void deleteMessage_invalidId_NotFound() {
        HttpResponse<String> response = deleteMessage(0);

        assertThat(response.body()).isEmpty();
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.getCode());
    }

    @Test
    void deleteMessage_messageFound_OK() {
        createMessage("message");
        createMessage("message2");
        HttpResponse<String> response = deleteMessage(0);

        assertThat(response.body()).isEqualTo("message");
        assertThat(response.statusCode()).isEqualTo(OK.getCode());
        assertThat(getMessage(1).body()).isEqualTo("message2");
    }

    private HttpResponse<String> getMessages() {
        return sendRequest(request("/messages")
                .GET()
                .build());
    }

    private HttpResponse<String> getMessage(int id) {
        return sendRequest(request("/messages/" + id)
                .GET()
                .build());
    }

    private HttpResponse<String> createMessage(String content) {
        return sendRequest(request("/messages")
                .POST(createContent(content))
                .build());
    }

    private HttpResponse<String> deleteMessage(int id) {
        return sendRequest(request("/messages/" + id)
                .DELETE()
                .build());
    }
}
