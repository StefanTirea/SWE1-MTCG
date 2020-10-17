package mtcg.model.interfaces;

import mtcg.model.enums.ElementType;

public interface BattleCard extends Card {

    float getDamage();

    ElementType getElementType();

    int getMana();
}
