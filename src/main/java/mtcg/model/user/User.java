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

import static java.util.stream.Collectors.toList;

@Builder
@Data
public class User implements BasicUser {

    private final Long id;
    private final String username;
    @Singular
    private final List<String> roles;
    private final List<Item> inventory;
    private List<BattleCard> deck;
    private int coins;
    private int elo;
    private int gamesPlayed;
    private int gamesWon;

    public List<Card> getStack() {
        return inventory.stream()
                .filter(Card.class::isInstance)
                .map(Card.class::cast)
                .collect(toList());
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
                deck = cards;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean addItem(Item item) {
        synchronized (inventory) {
            return inventory.add(item);
        }
    }

    public boolean spentCoins(int count) {
        if (coins >= count) {
            coins -= count;
            return true;
        } else {
            return false;
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
