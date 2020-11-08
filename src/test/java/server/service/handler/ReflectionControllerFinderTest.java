package server.service.handler;

import org.junit.jupiter.api.Test;
import server.controller.MessageController;
import server.model.http.PathHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static server.model.enums.HttpMethod.DELETE;
import static server.model.enums.HttpMethod.GET;
import static server.model.enums.HttpMethod.POST;
import static server.model.enums.HttpMethod.PUT;
import static server.service.handler.ReflectionControllerFinder.getRegex;

class ReflectionControllerFinderTest {

    @Test
    void scanForControllers_verifyMessageController_worksProperly() {
        List<PathHandler> pathHandlers = spy(new ArrayList<>());
        List<Object> controllers = spy(new ArrayList<>());

        ReflectionControllerFinder.scanForControllers(pathHandlers::add, controllers::add);
        List<PathHandler> messagePathHandlers = pathHandlers.stream()
                .filter(p -> p.getMethod().getDeclaringClass().equals(MessageController.class))
                .collect(Collectors.toList());

        assertThat(controllers)
                .hasSizeGreaterThanOrEqualTo(1)
                .singleElement()
                .isOfAnyClassIn(MessageController.class);
        assertThat(messagePathHandlers)
                .hasSize(5)
                .flatExtracting(p -> p.getMethod().getName(),
                        PathHandler::getHttpMethod,
                        PathHandler::getPath,
                        PathHandler::getPathVariableTypes)
                .containsExactlyInAnyOrder(
                        "getMessage", GET, "/messages/{id}", List.of(int.class),
                        "getMessages", GET, "/messages", emptyList(),
                        "createMessage", POST, "/messages", emptyList(),
                        "updateMessage", PUT, "/messages/{id}", List.of(int.class),
                        "deleteMessage", DELETE, "/messages/{id}", List.of(int.class));

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
