package mtcg.model.user;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import mtcg.model.interfaces.BasicUser;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.Item;
import mtcg.model.items.CardPackage;

import javax.security.auth.Subject;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Builder
@Data
public class User implements BasicUser {

    @Singular("inventory")
    private final List<Item> inventory;
    @Singular
    private final List<List<BattleCard>> decks;
    @Singular
    private final List<String> roles;
    private String username;
    private int coins;

    public List<Card> getStack() {
        return inventory.stream()
                .filter(Card.class::isInstance)
                .map(Card.class::cast)
                .collect(toList());
    }

    public List<BattleCard> getDeck() {
        if (decks.isEmpty()) {
            return emptyList();
        }
        return decks.get(0);
    }

    public List<Card> openItemContainer() {
        synchronized (inventory) {
            Optional<CardPackage> cardPackage = inventory.stream()
                    .filter(item -> item instanceof CardPackage)
                    .findAny()
                    .map(CardPackage.class::cast);
            cardPackage.ifPresent(inventory::remove);
            List<Card> cards = cardPackage.stream()
                    .flatMap(container -> container.open().stream())
                    .collect(toList());
            inventory.addAll(cards);
            return cards;
        }
    }

    public boolean createDeck(int pos1, int pos2, int pos3, int pos4) {
        synchronized (inventory) {
            List<BattleCard> cards = Set.of(pos1, pos2, pos3, pos4).stream()
                    .filter(index -> index >= 0 && index < inventory.size())
                    .map(inventory::get)
                    .filter(BattleCard.class::isInstance)
                    .map(BattleCard.class::cast)
                    .collect(toList());
            if (cards.size() == 4) {
                decks.add(cards);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
