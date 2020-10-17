package mtcg.model.user;

import lombok.Data;
import mtcg.model.interfaces.BasicUser;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.Item;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Data
public class User implements BasicUser {

    private String username;
    private String passwordHash;
    private String token;
    private int coins;
    private List<Item> inventory;
    private List<List<Card>> decks;

    public List<Card> getStack() {
        return inventory.stream()
                .filter(Card.class::isInstance)
                .map(Card.class::cast)
                .collect(toList());
    }

    public List<Card> getDeck() {
        if (decks.isEmpty()) {
            return emptyList();
        }
        return decks.get(0);
    }
}
