package mtcg.model.cards;

import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.interfaces.Attackable;
import mtcg.model.interfaces.Card;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MonsterCard extends BasicBattleCard implements Attackable {

    private final MonsterType monsterType;

    public MonsterCard(String name, String description, int mana, float damage, MonsterType monsterType, ElementType elementType) {
        super(name, description, mana, damage, elementType);
        this.monsterType = monsterType;
    }

    @Override
    public boolean attack(Card otherCard) {
        return false;
    }
}
