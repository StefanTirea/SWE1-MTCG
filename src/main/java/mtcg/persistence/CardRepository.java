package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.cards.MonsterCard;
import mtcg.model.cards.SpellCardAttacking;
import mtcg.model.entity.CardEntity;
import mtcg.model.entity.TokenEntity;
import mtcg.model.interfaces.BattleCard;
import mtcg.persistence.base.BaseRepository;
import mtcg.persistence.base.ConnectionPool;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CardRepository extends BaseRepository<CardEntity> {

    public CardRepository(ConnectionPool connectionPool) {
        super(connectionPool, CardEntity.class);
    }

    public List<BattleCard> getBattleCardsByUser(Long userId) {
        return getEntitiesByFilter("user_id", userId).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public List<BattleCard> getBattleCardsByIds(List<Long> cardIds) {
        return getEntitiesByFilter("id in", cardIds).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private BattleCard convert(CardEntity cardEntity) {
        if (cardEntity.getMonsterType() != null) {
            return MonsterCard.builder()
                    .id(cardEntity.getId())
                    .name(cardEntity.getName())
                    .elementType(cardEntity.getElementType())
                    .monsterType(cardEntity.getMonsterType())
                    .damage(cardEntity.getDamage())
                    .build();
        } else {
            return SpellCardAttacking.builder()
                    .id(cardEntity.getId())
                    .name(cardEntity.getName())
                    .elementType(cardEntity.getElementType())
                    .damage(cardEntity.getDamage())
                    .build();
        }
    }
}
