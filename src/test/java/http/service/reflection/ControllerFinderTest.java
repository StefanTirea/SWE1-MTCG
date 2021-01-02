package http.service.reflection;

import http.model.http.PathHandler;
import http.service.handler.FilterManager;
import mtcg.controller.MessageController;
import mtcg.service.MessageService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static http.model.enums.HttpMethod.DELETE;
import static http.model.enums.HttpMethod.GET;
import static http.model.enums.HttpMethod.POST;
import static http.model.enums.HttpMethod.PUT;
import static http.service.reflection.ControllerFinder.getRegex;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ControllerFinderTest {

    @Test
    void scanForControllers_verifyMessageController_worksProperly() {
        List<PathHandler> pathHandlers = spy(new ArrayList<>());
        List<Object> controllers = spy(new ArrayList<>());

        ControllerFinder.scanForControllers(Map.of(MessageController.class, new MessageController(new MessageService())), pathHandlers::add, controllers::add);
        List<PathHandler> messagePathHandlers = pathHandlers.stream()
                .filter(p -> p.getMethod().getDeclaringClass().equals(MessageController.class))
                .collect(Collectors.toList());

        assertThat(controllers)
                .hasSizeGreaterThanOrEqualTo(1)
                .hasAtLeastOneElementOfType(MessageController.class);
        assertThat(messagePathHandlers)
                .hasSize(5)
                .flatExtracting(p -> p.getMethod().getName(),
                        PathHandler::getHttpMethod,
                        PathHandler::getPath)
                .containsExactlyInAnyOrder(
                        "getMessage", GET, "/messages/{id}",
                        "getMessages", GET, "/messages",
                        "createMessage", POST, "/messages",
                        "updateMessage", PUT, "/messages/{id}",
                        "deleteMessage", DELETE, "/messages/{id}");

        verify(controllers, atLeast(1)).add(any());
        verify(pathHandlers, atLeast(5)).add(any());
    }

    @Test
    void getRegex_convertPath_mappedCorrectly() {
        assertThat(getRegex("/")).isEqualTo("^\\/\\/?$");
        assertThat(getRegex("/test/path")).isEqualTo("^\\/test\\/path\\/?$");
        assertThat(getRegex("/test/{id}/path/{name}")).isEqualTo("^\\/test\\/([^/]+)\\/path\\/([^/]+)\\/?$");
        assertThat(getRegex("")).isEqualTo("^\\/?$");
    }
}
