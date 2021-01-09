package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Get;
import http.model.annotation.Post;
import http.model.annotation.Put;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import http.model.enums.HttpStatus;
import http.model.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.user.User;
import mtcg.service.ItemService;

import java.util.List;

@Controller
@Secured
@RequiredArgsConstructor
public class CardController {

    private final ItemService itemService;

    @Put("/api/decks")
    public HttpResponse createDeck(User user, @RequestBody List<Number> cardIds) {
        if (itemService.createDeck(user, cardIds)) {
            return HttpResponse.builder().build();
        } else {
            return HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).content("Deck already configured!").build();
        }
    }

    @Get("/api/cards")
    @Secured
    public List<BattleCard> getCardsFromUser(User user) {
        return user.getStack();
    }

    @Get("/api/decks")
    @Secured
    public List<BattleCard> getDeckFromUser(User user) {
        return user.getDeck();
    }
}
