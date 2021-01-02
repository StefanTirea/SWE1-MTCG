package mtcg.model.interfaces;

import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;

public interface BattleCard extends Card, Attackable {

    int getDamage();

    ElementType getElementType();

    MonsterType getMonsterType();

    int getMana();

    Effectiveness getEffectiveMultiplier(ElementType otherElementType);

    int getDamageWithEffectiveMultiplier(BattleCard card, ElementType enemyElementType);
}
