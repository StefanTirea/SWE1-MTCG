package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.cards.MonsterCard;
import mtcg.model.cards.SpellCardAttacking;
import mtcg.model.entity.CardEntity;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.interfaces.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CardRepository extends BaseRepository<CardEntity> {

    public CardRepository() {
        super(CardEntity.class);
    }

    public List<BattleCard> getBattleCardsByUser(Long userId) {
        return selectEntitiesByFilter("user_id", userId).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public Optional<BattleCard> getBattleCard(Long cardId) {
        return selectEntityById(cardId)
                .map(this::convert);
    }

    public List<BattleCard> getBattleCardsByIds(List<Long> cardIds) {
        return selectEntitiesByFilter("id in", cardIds).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public List<Long> saveBattleCardWithoutUser(List<? extends Item> items) {
        return items.stream()
                .filter(item -> item instanceof BattleCard)
                .map(item -> (BattleCard) item)
                .map(battleCard -> CardEntity.builder()
                        .name(battleCard.getName())
                        .damage(battleCard.getDamage())
                        .elementType(battleCard.getElementType())
                        .monsterType(battleCard.getMonsterType())
                        .build())
                .map(this::insert)
                .collect(Collectors.toList());
    }

    public boolean updateBattleCards(List<Long> cardIds, Long userId) {
        cardIds.stream()
                .map(cardId -> CardEntity.builder()
                        .id(cardId)
                        .userId(userId)
                        .build())
                .forEach(this::update);
        return true;
    }

    public void updateCardLockStatus(Long cardId, Long userId, boolean locked) {
        update(CardEntity.builder()
                .id(cardId)
                .userId(userId)
                .locked(locked)
                .build());
    }

    public boolean isCardLocked(Long cardId) {
        return selectEntityById(cardId).map(CardEntity::getLocked).orElse(true);
    }

    private BattleCard convert(CardEntity cardEntity) {
        if (cardEntity.getMonsterType() != null) {
            return MonsterCard.builder()
                    .id(cardEntity.getId())
                    .name(cardEntity.getName())
                    .elementType(ElementType.valueOf(cardEntity.getElementType()))
                    .monsterType(MonsterType.valueOf(cardEntity.getMonsterType()))
                    .damage(cardEntity.getDamage())
                    .build();
        } else {
            return SpellCardAttacking.builder()
                    .id(cardEntity.getId())
                    .name(cardEntity.getName())
                    .elementType(ElementType.valueOf(cardEntity.getElementType()))
                    .damage(cardEntity.getDamage())
                    .build();
        }
    }
}
