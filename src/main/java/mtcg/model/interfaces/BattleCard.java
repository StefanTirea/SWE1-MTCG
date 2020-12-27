package mtcg.model.interfaces;

import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;

public interface BattleCard extends Card, Attackable {

    int getDamage();

    ElementType getElementType();

    int getMana();

    Effectiveness getEffectiveMultiplier(ElementType otherElementType);

    int getDamageWithEffectiveMultiplier(BattleCard card, ElementType enemyElementType);
}
