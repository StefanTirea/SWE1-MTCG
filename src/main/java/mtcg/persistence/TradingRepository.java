package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.entity.TradingEntity;
import mtcg.model.user.TradingOffer;
import mtcg.persistence.base.BaseRepository;
import mtcg.persistence.base.ConnectionPool;

@Component
public class TradingRepository extends BaseRepository<TradingEntity> {

    public TradingRepository(ConnectionPool connectionPool) {
        super(connectionPool, TradingEntity.class);
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
