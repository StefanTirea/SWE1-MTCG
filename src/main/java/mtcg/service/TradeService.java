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
        if (cardRepository.isCardLocked(tradingOffer.getCardId())) {
            return false;
        }
        cardRepository.updateCardLockStatus(tradingOffer.getCardId(), user.getId(), true);
        if (user.getDeck().stream().anyMatch(card -> card.getId().equals(tradingOffer.getCardId()))) {
            user.resetDeck();
        }
        tradingRepository.saveTradingOffer(tradingOffer, user.getId());
        userRepository.updateUser(user);
        return true;
    }

    public boolean acceptTradeOffer(User user, BattleCard card, Long tradeId) {
        return tradingRepository.selectEntityById(tradeId)
                .filter(trade -> !trade.getUserId().equals(user.getId()))
                .map(trade -> {
                    if (trade.getMinDamage() <= card.getDamage()
                            && (card.getElementType().name().equals(trade.getType())
                            || (card.getMonsterType() != null && card.getMonsterType().name().equals(trade.getType())))) {
                        cardRepository.updateCardLockStatus(card.getId(), trade.getUserId(), false);
                        cardRepository.updateCardLockStatus(trade.getCardId(), user.getId(), false);
                        // Delete trade
                        if (user.getDeck().contains(card)) {
                            user.resetDeck();
                        }
                        userRepository.updateUser(user);
                        tradingRepository.delete(tradeId);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public List<TradingOffer> getAllTradingOffers() {
        return tradingRepository.selectEntitiesByFilter().stream()
                .map(entity -> TradingOffer.builder()
                        .id(entity.getId())
                        .card(cardRepository.getBattleCard(entity.getCardId()).orElseThrow())
                        .minDamage(entity.getMinDamage())
                        .type(entity.getType())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean deleteTradeOffer(User user, Long tradeId) {
        return tradingRepository.selectEntityById(tradeId)
                .filter(trade -> trade.getUserId().equals(user.getId()))
                .map(trade -> {
                    tradingRepository.delete(tradeId);
                    cardRepository.updateCardLockStatus(trade.getCardId(), user.getId(), false);
                    return true;
                })
                .orElse(false);
    }
}
