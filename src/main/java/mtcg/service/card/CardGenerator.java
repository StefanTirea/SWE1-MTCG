package mtcg.service.card;

import http.model.annotation.Component;
import lombok.Data;
import mtcg.model.cards.MonsterCard;
import mtcg.model.cards.SpellCardAttacking;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.interfaces.Card;
import mtcg.model.items.CardPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
@Data
public class CardGenerator {

    // TODO: add modifier for each adjective (plus damage or minus damage)
    private final List<String> cardAdjectives = List.of("Happy", "Crazy", "Spooky", "Sleepy", "Magical", "Legendary");

    public MonsterCard generateMonsterCard() {
        MonsterType monsterType = getRandomMonsterType();
        return MonsterCard.builder()
                .name(getRandomMonsterName(monsterType))
                .monsterType(monsterType)
                .damage(getRandomDamageValue(monsterType))
                .elementType(getRandomElementType())
                .build();
    }

    public SpellCardAttacking generateSpellCardAttacking() {
        ElementType elementType = getRandomElementType();
        return SpellCardAttacking.builder()
                .name(elementType.getText() + " Spell")
                .damage(ThreadLocalRandom.current().nextInt(5, 20))
                .elementType(elementType)
                .build();
    }

    public CardPackage generateCardPackage(int cardCount) {
        int monsterCardsCount = ThreadLocalRandom.current().nextInt(cardCount + 1);
        int spellCardsCount = cardCount - monsterCardsCount;
        return CardPackage.builder()
                .name(getRandomMonsterName(getRandomMonsterType()) + " Mystery Package")
                .description(String.format("This is a Card Package with %d random Cards!", cardCount))
                .content(getRandomCards(monsterCardsCount, spellCardsCount))
                .build();
    }

    private List<Card> getRandomCards(int monsterCards, int spellCards) {
        List<Card> cards = new ArrayList<>();
        IntStream.range(0, monsterCards).forEach(i -> cards.add(generateMonsterCard()));
        IntStream.range(0, spellCards).forEach(i -> cards.add(generateSpellCardAttacking()));
        return cards;
    }

    private String getRandomMonsterName(MonsterType monsterType) {
        return String.format("%s %s", cardAdjectives.get(new Random().nextInt(cardAdjectives.size())), monsterType.getText());
    }

    private MonsterType getRandomMonsterType() {
        return MonsterType.values()[ThreadLocalRandom.current().nextInt(MonsterType.values().length)];
    }

    private ElementType getRandomElementType() {
        return ElementType.values()[ThreadLocalRandom.current().nextInt(ElementType.values().length)];
    }

    private int getRandomDamageValue(MonsterType monsterType) {
        return ThreadLocalRandom.current().nextInt(monsterType.getMinDamage(), monsterType.getMaxDamage() + 1);
    }
}
