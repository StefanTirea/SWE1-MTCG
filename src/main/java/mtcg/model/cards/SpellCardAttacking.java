package mtcg.model.cards;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.RuleResult;
import mtcg.model.interfaces.Attackable;
import mtcg.model.interfaces.BattleCard;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SpellCardAttacking extends BasicBattleCard implements Attackable {

    @Builder
    public SpellCardAttacking(String name, int mana, int damage, ElementType elementType) {
        super(name, mana, Math.max(damage, 0), elementType);
    }

    public RuleResult attack(BattleCard otherCard) {
        RuleResult result = super.attack(otherCard);
        if (!result.equals(RuleResult.NOTHING)) {
            return result;
        }

        if (otherCard instanceof MonsterCard) {
            int attack = getDamageWithEffectiveMultiplier(this, otherCard.getElementType());
            int defender = getDamageWithEffectiveMultiplier(otherCard, getElementType());
            if (attack == defender) {
                return RuleResult.NOTHING;
            } else if (attack > defender) {
                return RuleResult.ATTACKER;
            } else {
                return RuleResult.DEFENDER;
            }
        } else if (otherCard instanceof SpellCardAttacking) {
            switch (getEffectiveMultiplier(otherCard.getElementType())) {
                case EFFECTIVE:
                    return RuleResult.ATTACKER;
                case NOT_EFFECTIVE:
                    return RuleResult.DEFENDER;
                default:
                    return RuleResult.NOTHING;
            }
        } else {
            throw new UnsupportedOperationException("The Card being attacked has an unknown type!");
        }
    }
}
