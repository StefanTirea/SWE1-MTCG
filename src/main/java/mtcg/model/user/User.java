package mtcg.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import mtcg.model.battle.BattleReport;
import mtcg.model.enums.BattleStatus;
import mtcg.model.interfaces.BasicUser;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.Item;
import mtcg.model.items.CardPackage;

import javax.security.auth.Subject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Builder
@Data
public class User implements BasicUser {

    @JsonIgnore
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

    public List<BattleCard> getStack() {
        return inventory.stream()
                .filter(BattleCard.class::isInstance)
                .map(BattleCard.class::cast)
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

    public boolean createDeck(List<Long> cardIds) {
        if (cardIds.size() != 4) {
            throw new IllegalArgumentException("Deck size must be 4!");
        }
        synchronized (inventory) {
            List<BattleCard> deckCards = inventory.stream()
                    .filter(BattleCard.class::isInstance)
                    .map(BattleCard.class::cast)
                    .filter(card -> cardIds.contains(card.getId()))
                    .collect(toList());
            if (deckCards.size() == 4) {
                deck = deckCards;
                return true;
            } else {
                return false;
            }
        }
    }

    public void resetDeck() {
        deck = Collections.emptyList();
    }

    public boolean addItem(Item item) {
        synchronized (inventory) {
            return inventory.add(item);
        }
    }

    public boolean removeItem(Item item) {
        synchronized (inventory) {
            return inventory.remove(item);
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

    public void updateStats(BattleReport battleReport) {
        elo += battleReport.getEloChange();
        gamesPlayed++;
        if (BattleStatus.WON.equals(battleReport.getOutcome())) {
            gamesWon++;
        }
    }

    @Override
    @JsonIgnore
    public String getName() {
        return username;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
