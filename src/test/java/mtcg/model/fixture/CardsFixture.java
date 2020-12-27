package mtcg.model.fixture;

import mtcg.model.cards.MonsterCard;
import mtcg.model.cards.SpellCardAttacking;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.items.CardPackage;

import java.util.ArrayList;
import java.util.List;

public class CardsFixture {

    public static MonsterCard monsterCard() {
        return MonsterCard.builder()
                .name("Angry Chicken")
                .damage(10)
                .monsterType(MonsterType.CHICKEN)
                .elementType(ElementType.FIRE)
                .build();
    }

    public static SpellCardAttacking spellCard() {
        return SpellCardAttacking.builder()
                .name("Fire Spell")
                .damage(10)
                .elementType(ElementType.FIRE)
                .build();
    }

    public static CardPackage cardPackage() {
        return CardPackage.builder()
                .content(new ArrayList<>(List.of(monsterCard(), spellCard())))
                .build();
    }
}
