package mtcg.model.interfaces;

import http.model.interfaces.Authentication;

import java.util.List;

public interface BasicUser extends Authentication {

    int getCoins();

    List<Item> getInventory();

    List<BattleCard> getDeck();

    List<Card> getStack();
}
