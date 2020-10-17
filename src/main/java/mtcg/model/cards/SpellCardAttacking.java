package mtcg.model.cards;

import mtcg.model.enums.ElementType;
import mtcg.model.interfaces.Attackable;
import mtcg.model.interfaces.Card;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SpellCardAttacking extends BasicBattleCard implements Attackable {

    public SpellCardAttacking(String name, String description, int mana, float damage, ElementType elementType) {
        super(name, description, mana, damage, elementType);
    }

    @Override
    public boolean attack(Card otherCard) {
        return false;
    }
}
