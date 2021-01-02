package mtcg.service;

import http.model.annotation.Component;
import lombok.RequiredArgsConstructor;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.user.TradingOffer;
import mtcg.model.user.User;
import mtcg.persistence.CardRepository;
import mtcg.persistence.TradingRepository;
import mtcg.persistence.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TradeService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final TradingRepository tradingRepository;

    public boolean createTradeOffer(User user, TradingOffer tradingOffer) {
        cardRepository.updateCardLockStatus(tradingOffer.getCardId(), user.getId(), true);
        if (user.getDeck().stream().anyMatch(card -> card.getId().equals(tradingOffer.getCardId()))) {
            user.resetDeck();
        }
        tradingRepository.saveTradingOffer(tradingOffer, user.getId());
        userRepository.updateUser(user);
        return true;
    }

    public boolean acceptTradeOffer(User user, BattleCard card, Long tradeId) {
        tradingRepository.getEntityById(tradeId).ifPresent(trade -> {
            if (trade.getMinDamage() <= card.getDamage()
                    && (card.getElementType().name().equals(trade.getType())
                    || card.getMonsterType().name().equals(trade.getType()))) {
                cardRepository.updateCardLockStatus(card.getId(), trade.getUserId(), false);
                cardRepository.updateCardLockStatus(trade.getCardId(), user.getId(), false);
                // Delete trade
                if (user.getDeck().contains(card)) {
                    user.resetDeck();
                }
                userRepository.updateUser(user);
                tradingRepository.delete(tradeId);
            }
        });
        return true;
    }

    public List<TradingOffer> getAllTradingOffers() {
        return tradingRepository.getEntitiesByFilter().stream()
                .map(entity -> TradingOffer.builder()
                        .id(entity.getId())
                        .card(cardRepository.getBattleCard(entity.getCardId()).orElseThrow())
                        .minDamage(entity.getMinDamage())
                        .type(entity.getType())
                        .build())
                .collect(Collectors.toList());
    }
}
