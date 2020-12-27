package mtcg.model.cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.RuleResult;
import mtcg.model.interfaces.BattleCard;
import mtcg.service.card.CardRules;

import java.util.Optional;

@Data
@AllArgsConstructor
public abstract class BasicBattleCard implements BattleCard {

    private final String name;
    private final int mana;
    private final int damage;
    @NonNull
    private final ElementType elementType;

    @Override
    public Effectiveness getEffectiveMultiplier(ElementType elementType) {
        if (getElementType().equals(elementType)) {
            return Effectiveness.NO_EFFECT;
        } else if (elementType.getWeaknessEnum().equals(getElementType())) {
            return Effectiveness.EFFECTIVE;
        } else {
            return Effectiveness.NOT_EFFECTIVE;
        }
    }

    @Override
    public int getDamageWithEffectiveMultiplier(BattleCard card, ElementType enemyElementType) {
        return (int) (card.getDamage() * card.getEffectiveMultiplier(enemyElementType).getPercentage());
    }

    @Override
    public RuleResult attack(BattleCard otherCard) {
        RuleResult result = null;
        if (this instanceof MonsterCard && otherCard instanceof MonsterCard) {
            result = CardRules.checkRulesMonsterVsMonster((MonsterCard) this, (MonsterCard) otherCard);
        } else if (this instanceof MonsterCard && otherCard instanceof SpellCardAttacking) {
            result = CardRules.checkRulesMonsterVsSpell((MonsterCard) this, (SpellCardAttacking) otherCard, false);
        } else if (this instanceof SpellCardAttacking && otherCard instanceof MonsterCard) {
            result = CardRules.checkRulesMonsterVsSpell((MonsterCard) otherCard, (SpellCardAttacking) this, true);
        }

        return Optional.ofNullable(result)
                .orElse(RuleResult.NOTHING);
    }
}
