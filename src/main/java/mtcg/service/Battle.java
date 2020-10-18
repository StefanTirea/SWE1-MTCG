package mtcg.service;

import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.interfaces.Card;
import mtcg.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Log
@Data
public class Battle {

    // Block if no deck!
    public void battle(User userOne, User userTwo) {
        List<BattleCard> userOneDeck = new ArrayList<>(userOne.getDeck());
        List<BattleCard> userTwoDeck = new ArrayList<>(userTwo.getDeck());
        BattleCard attacker, defender;
        boolean userOneAttacks;
        for (int i = 0; i < 100; i++) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                userOneAttacks = true;
                attacker = drawRandomCardFromDeck(userOneDeck);
                defender = drawRandomCardFromDeck(userTwoDeck);
            } else {
                userOneAttacks = false;
                attacker = drawRandomCardFromDeck(userTwoDeck);
                defender = drawRandomCardFromDeck(userOneDeck);
            }

            if (userOneAttacks) {
                if (attacker.attack(defender)) {
                    log.info(String.format("User1 %s wins against User2 %s", attacker, defender));
                    userTwoDeck.remove(defender);
                    userOneDeck.add(defender);
                } else {
                    log.info(String.format("User1 %s loses against User2 %s", attacker, defender));
                    userOneDeck.remove(attacker);
                    userTwoDeck.add(attacker);
                }
            } else {
                if (attacker.attack(defender)) {
                    log.info(String.format("User2 %s wins against User1 %s", attacker, defender));
                    userOneDeck.remove(defender);
                    userTwoDeck.add(defender);
                } else {
                    log.info(String.format("User2 %s loses against User1 %s", attacker, defender));
                    userTwoDeck.remove(attacker);
                    userOneDeck.add(attacker);
                }
            }

            if (userOneDeck.isEmpty()) {
                userWinsGame(userTwo);
                return;
            } else if (userTwoDeck.isEmpty()) {
                userWinsGame(userOne);
                return;
            }
        }
        log.info("DRAW!");
    }

    private BattleCard drawRandomCardFromDeck(List<BattleCard> cards) {
        return cards.get(ThreadLocalRandom.current().nextInt(0, cards.size()));
    }

    private void userWinsGame(User user) {
        log.info("USER WINS: " + user.toString());
    }
}
