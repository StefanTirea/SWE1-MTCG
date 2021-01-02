package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.entity.TradingEntity;
import mtcg.model.user.TradingOffer;
import http.service.persistence.ConnectionPool;

@Component
public class TradingRepository extends BaseRepository<TradingEntity> {

    public TradingRepository() {
        super(TradingEntity.class);
    }

    public boolean saveTradingOffer(TradingOffer tradingOffer, Long userId) {
        insert(TradingEntity.builder()
                .cardId(tradingOffer.getCardId())
                .userId(userId)
                .minDamage(tradingOffer.getMinDamage())
                .type(tradingOffer.getType())
                .build());
        return true;
    }
}
