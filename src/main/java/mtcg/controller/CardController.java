package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Post;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import http.model.enums.HttpStatus;
import http.model.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import mtcg.model.user.User;
import mtcg.service.ItemService;

import java.util.List;

@Controller
@Secured
@RequiredArgsConstructor
public class CardController {

    private final ItemService itemService;

    @Post("/api/decks")
    public HttpResponse createDeck(User user, @RequestBody List<Number> cardIds) {
        if (itemService.createDeck(user, cardIds)) {
            return HttpResponse.builder().build();
        } else {
            return HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build();
        }
    }
}
