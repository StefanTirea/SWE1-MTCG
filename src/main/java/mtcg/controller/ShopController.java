package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Get;
import http.model.annotation.PathVariable;
import http.model.annotation.Post;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import http.model.enums.HttpStatus;
import http.model.exception.BadRequestException;
import http.model.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import mtcg.model.interfaces.Card;
import mtcg.model.items.CardPackage;
import mtcg.model.user.TradingOffer;
import mtcg.model.user.User;
import mtcg.persistence.CardRepository;
import mtcg.persistence.PackageRepository;
import mtcg.persistence.UserRepository;
import mtcg.service.ItemService;
import mtcg.service.card.CardGenerator;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Secured
public class ShopController {

    private final UserRepository userRepository;
    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;
    private final CardGenerator cardGenerator;
    private final ItemService itemService;

    @Post("/api/transactions/packages")
    public CardPackage buyPackage(User user) {
        if (user.spentCoins(5)) {
            CardPackage cardPackage = cardGenerator.generateCardPackage(5);
            user.addItem(cardPackage);
            userRepository.updateUser(user);
            packageRepository.savePackage(cardPackage, user.getId());
            return cardPackage;
        } else {
            throw new BadRequestException("Not enough money! 5 coins required, you have " + user.getCoins());
        }
    }

    @Post("/api/transactions/trading")
    public HttpResponse createTradingOffer(User user, @RequestBody TradingOffer tradingOffer) {
        return user.getStack().stream()
                .filter(card -> card.getId().equals(tradingOffer.getCardId()))
                .findFirst()
                .map(card -> itemService.createTradeOffer(user, tradingOffer))
                .map(result -> HttpResponse.builder().httpStatus(HttpStatus.CREATED).build())
                .orElse(HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build());
    }

    @Get("/api/trading")
    public List<TradingOffer> listTradingOffers() {
        return itemService.getAllTradingOffers();
    }

    @Get("/api/transactions/trading/{tradeId}/{cardId}")
    public HttpResponse acceptTradingOffer(User user, @PathVariable Long tradeId, @PathVariable Long cardId) {
        return user.getStack().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .map(card -> itemService.acceptTradeOffer(user, card, tradeId))
                .map(result -> HttpResponse.builder().httpStatus(HttpStatus.CREATED).build())
                .orElse(HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build());
    }

    @Get("/api/packages")
    public List<Card> openPackage(User user) {
        List<Card> cards = user.openItemContainer();
        cardRepository.updateBattleCards(cards, user.getId());
        // TODO: Delete Package when opened from DB
        return cards;
    }
}
