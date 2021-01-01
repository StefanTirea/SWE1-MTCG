package mtcg.model.battle;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class BattleStats {

    private String username;
    private int gamesPlayed;
    private String winRate;
    private int elo;
    private List<BattleReport> gamesHistory;
}
