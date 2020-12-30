package mtcg.model.cards;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.enums.RuleResult;
import mtcg.model.interfaces.BattleCard;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MonsterCard extends BasicBattleCard {

    @NonNull
    private final MonsterType monsterType;

    @Builder
    public MonsterCard(Long id, String name, int mana, int damage, MonsterType monsterType, ElementType elementType) {
        super(id, name, mana, Math.max(damage, 0), elementType);
        this.monsterType = monsterType;
    }

    @Override
    public RuleResult attack(BattleCard otherCard) {
        RuleResult result = super.attack(otherCard);
        if (!RuleResult.NOTHING.equals(result)) {
            return result;
        }

        if (otherCard instanceof MonsterCard) {
            int attack = getDamage();
            int defender = otherCard.getDamage();
            if (attack == defender) {
                return RuleResult.NOTHING;
            } else if (attack > defender) {
                return RuleResult.ATTACKER;
            } else {
                return RuleResult.DEFENDER;
            }
        } else if (otherCard instanceof SpellCardAttacking) {
            int attack = getDamageWithEffectiveMultiplier(this, otherCard.getElementType());
            int defender = getDamageWithEffectiveMultiplier(otherCard, getElementType());
            if (attack == defender) {
                return RuleResult.NOTHING;
            } else if (attack > defender) {
                return RuleResult.ATTACKER;
            } else {
                return RuleResult.DEFENDER;
            }
        } else {
            throw new UnsupportedOperationException("The Card being attacked has an unknown type!");
        }
    }
}
