package mtcg.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mtcg.model.enums.BattleStatus;
import mtcg.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mtcg.model.enums.BattleStatus.LOST;
import static mtcg.model.enums.BattleStatus.WAITING;
import static mtcg.model.enums.BattleStatus.WON;

@Getter
@EqualsAndHashCode
@ToString
public class Battle {

    private User playerOne;
    @Setter
    private User playerTwo;
    @Setter
    private BattleStatus status;
    private final Map<String, BattleStatus> statusPlayers = new HashMap<>();
    @Setter
    private int roundsPlayed;
    private final List<String> logs = new ArrayList<>();

    @Builder
    public Battle(User playerOne) {
        this.playerOne = playerOne;
        this.status = WAITING;
        this.roundsPlayed = 100;
    }

    public void setPlayerResult(BattleStatus playerOneStatus) {
        statusPlayers.put(playerOne.getUsername(), playerOneStatus);
        BattleStatus playerTwoStatus = switch (playerOneStatus) {
            case WON -> LOST;
            case LOST -> WON;
            default -> playerOneStatus;
        };
        statusPlayers.put(playerTwo.getUsername(), playerTwoStatus);
    }

    public void addLogEntry(String log) {
        logs.add(log);
    }

    public BattleReport generateReport(String username) {
        return BattleReport.builder()
                .outcome(statusPlayers.get(username))
                .log(logs)
                .rounds(roundsPlayed)
                .build();
    }
}
