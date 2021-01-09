package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Delete;
import http.model.annotation.Get;
import http.model.annotation.PathVariable;
import http.model.annotation.Post;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import http.model.enums.HttpStatus;
import http.model.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import mtcg.model.interfaces.Card;
import mtcg.model.items.CardPackage;
import mtcg.model.user.TradingOffer;
import mtcg.model.user.User;
import mtcg.service.ItemService;
import mtcg.service.TradeService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Secured
public class ShopController {

    private final TradeService tradeService;
    private final ItemService itemService;

    @Post("/api/packages")
    public List<Card> openPackage(User user) {
        return itemService.openPackage(user);
    }

    @Post("/api/transactions/packages")
    public CardPackage buyPackage(User user) {
        return itemService.buyPackage(user);
    }

    @Post("/api/tradings")
    public HttpResponse createTradingOffer(User user, @RequestBody TradingOffer tradingOffer) {
        return user.getStack().stream()
                .filter(card -> card.getId().equals(tradingOffer.getCardId()))
                .findFirst()
                .map(card -> tradeService.createTradeOffer(user, tradingOffer))
                .map(result -> HttpResponse.builder().httpStatus(HttpStatus.CREATED).build())
                .orElse(HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build());
    }

    @Get("/api/tradings")
    public List<TradingOffer> listTradingOffers() {
        return tradeService.getAllTradingOffers();
    }

    @Delete("/api/tradings/{tradeId}")
    public boolean deleteTradeOffer(User user, @PathVariable Long tradeId) {
        return tradeService.deleteTradeOffer(user, tradeId);
    }

    @Post("/api/tradings/{tradeId}/{cardId}")
    public HttpResponse acceptTradingOffer(User user, @PathVariable Long tradeId, @PathVariable Long cardId) {
        return user.getStack().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .map(card -> tradeService.acceptTradeOffer(user, card, tradeId))
                .map(result -> HttpResponse.builder().httpStatus(HttpStatus.CREATED).content(result).build())
                .orElse(HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).content("Could not accept Trade!").build());
    }
}
