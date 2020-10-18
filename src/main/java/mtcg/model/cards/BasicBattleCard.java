package mtcg.model.cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.RuleResult;
import mtcg.model.interfaces.BattleCard;
import mtcg.service.CardRules;

import java.util.ArrayList;
import java.util.List;

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
    public float getDamageWithEffectiveMultiplier(BattleCard card, ElementType enemyElementType) {
        return card.getDamage() * card.getEffectiveMultiplier(enemyElementType).getPercentage();
    }

    @Override
    public Boolean attack(BattleCard otherCard) {
        List<RuleResult> results = new ArrayList<>();
        if (this instanceof MonsterCard && otherCard instanceof MonsterCard) {
            results.add(CardRules.checkRulesMonsterVsMonster((MonsterCard) this, (MonsterCard) otherCard));
        }
        if (this instanceof MonsterCard && otherCard instanceof SpellCardAttacking) {
            results.add(CardRules.checkRulesMonsterVsSpell((MonsterCard) this, (SpellCardAttacking) otherCard, false));
        } else if (this instanceof SpellCardAttacking && otherCard instanceof MonsterCard) {
            results.add(CardRules.checkRulesMonsterVsSpell((MonsterCard) otherCard, (SpellCardAttacking) this, true));
        }

        return results.stream()
                .filter(result -> !result.equals(RuleResult.NOTHING))
                .findAny()
                .map(result -> result.equals(RuleResult.ATTACKER))
                .orElse(null);
    }
}
