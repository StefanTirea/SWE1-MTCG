package mtcg.model.cards;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.interfaces.BattleCard;

import static java.util.Objects.nonNull;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MonsterCard extends BasicBattleCard {

    @NonNull
    private final MonsterType monsterType;

    @Builder
    public MonsterCard(String name, int mana, float damage, MonsterType monsterType, ElementType elementType) {
        super(name, mana, Math.max(damage, 0), elementType);
        this.monsterType = monsterType;
    }

    public Boolean attack(BattleCard otherCard) {
        Boolean result = super.attack(otherCard);
        if (nonNull(result)) {
            return result;
        }

        if (otherCard instanceof MonsterCard) {
            return getDamage() > otherCard.getDamage();
        } else if (otherCard instanceof SpellCardAttacking) {
            return getDamageWithEffectiveMultiplier(this, otherCard.getElementType()) > getDamageWithEffectiveMultiplier(otherCard, getElementType());
        } else {
            throw new UnsupportedOperationException("The Card being attacked has an unknown type!");
        }
    }
}
