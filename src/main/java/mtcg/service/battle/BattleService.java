package mtcg.service.battle;

import http.model.annotation.Component;
import lombok.extern.slf4j.Slf4j;
import mtcg.model.Battle;
import mtcg.model.BattleReport;
import mtcg.model.enums.BattleStatus;
import mtcg.model.enums.RuleResult;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static mtcg.model.enums.BattleStatus.DRAW;
import static mtcg.model.enums.BattleStatus.ENDED;
import static mtcg.model.enums.BattleStatus.IN_PROGRESS;
import static mtcg.model.enums.BattleStatus.LOST;
import static mtcg.model.enums.BattleStatus.WAITING;
import static mtcg.model.enums.BattleStatus.WON;

@Component
@Slf4j
public class BattleService {

    private final Map<String, Battle> battles = new ConcurrentHashMap<>();

    public void registerBattleId(String battleId, User user) {
        if (battles.containsKey(battleId)) {
            throw new IllegalStateException(battleId + " is already registered!");
        }
        Battle battle = Battle.builder()
                .playerOne(user)
                .build();
        battles.put(battleId, battle);
    }

    public void initBattle(String battleId, User user) {
        Battle battle = battles.get(battleId);
        if (battle != null && battle.getStatus().equals(WAITING)) {
            battle.setPlayerTwo(user);
            battle.setStatus(IN_PROGRESS);
            battle.setPlayerResult(DRAW); // Status used if not overridden later
            startBattle(battle);
        } else {
            throw new IllegalStateException(String.format("Battle has unexpected status %s", battle));
        }
    }

    public BattleStatus getBattleStatus(String battleId) {
        return battles.get(battleId).getStatus();
    }

    public BattleReport getBattleReport(String battleId, String username) {
        return battles.get(battleId).generateReport(username);
    }

    // Block if no deck!
    private void startBattle(Battle battle) {
        User userOne = battle.getPlayerOne();
        User userTwo = battle.getPlayerTwo();
        List<BattleCard> userOneDeck = new ArrayList<>(userOne.getDeck());
        List<BattleCard> userTwoDeck = new ArrayList<>(userTwo.getDeck());
        BattleCard card1, card2;
        for (int round = 1; round <= 100; round++) {
            card1 = drawRandomCardFromDeck(userOneDeck);
            card2 = drawRandomCardFromDeck(userTwoDeck);

            if (card1.attack(card2).equals(RuleResult.ATTACKER)) {
                battle.addLogEntry(String.format("User1 %s wins against User2 %s", card1, card2));
                userTwoDeck.remove(card2);
                userOneDeck.add(card2);
            } else {
                battle.addLogEntry(String.format("User1 %s loses against User2 %s", card1, card2));
                userOneDeck.remove(card1);
                userTwoDeck.add(card1);
            }

            if (userOneDeck.isEmpty()) {
                battle.setPlayerResult(LOST);
                battle.setRoundsPlayed(round);
                break;
            } else if (userTwoDeck.isEmpty()) {
                battle.setPlayerResult(WON);
                battle.setRoundsPlayed(round);
                break;
            }
        }
        battle.setStatus(ENDED);
    }

    private BattleCard drawRandomCardFromDeck(List<BattleCard> cards) {
        return cards.get(ThreadLocalRandom.current().nextInt(0, cards.size()));
    }
}
