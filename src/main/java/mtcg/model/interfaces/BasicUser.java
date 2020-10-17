package mtcg.model.interfaces;

import java.util.List;

public interface BasicUser extends Authentication {

    String getUsername();

    int getCoins();

    List<Item> getInventory();

    List<List<Card>> getDecks();

    List<Card> getDeck();

    List<Card> getStack();
}
