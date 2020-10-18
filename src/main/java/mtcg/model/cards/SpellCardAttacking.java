package mtcg.model.cards;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;
import mtcg.model.interfaces.Attackable;
import mtcg.model.interfaces.BattleCard;

import static java.util.Objects.nonNull;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SpellCardAttacking extends BasicBattleCard implements Attackable {

    @Builder
    public SpellCardAttacking(String name, int mana, float damage, ElementType elementType) {
        super(name, mana, Math.max(damage, 0), elementType);
    }

    public Boolean attack(BattleCard otherCard) {
        Boolean result = super.attack(otherCard);
        if (nonNull(result)) {
            return result;
        }

        if (otherCard instanceof MonsterCard) {
            return getDamageWithEffectiveMultiplier(this, otherCard.getElementType()) > getDamageWithEffectiveMultiplier(otherCard, getElementType());
        } else if (otherCard instanceof SpellCardAttacking) {
            return getEffectiveMultiplier(otherCard.getElementType()).equals(Effectiveness.EFFECTIVE);
        } else {
            throw new UnsupportedOperationException("The Card being attacked has an unknown type!");
        }
    }
}
