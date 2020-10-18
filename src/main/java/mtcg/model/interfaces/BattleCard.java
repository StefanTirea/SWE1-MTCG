package mtcg.model.interfaces;

import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;

public interface BattleCard extends Card, Attackable {

    float getDamage();

    ElementType getElementType();

    int getMana();

    Effectiveness getEffectiveMultiplier(ElementType otherElementType);

    float getDamageWithEffectiveMultiplier(BattleCard card, ElementType enemyElementType);
}
