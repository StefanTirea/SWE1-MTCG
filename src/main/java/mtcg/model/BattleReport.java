package mtcg.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import mtcg.model.enums.BattleStatus;

import java.util.List;

@Builder
@Data
public class BattleReport {

    private BattleStatus outcome;
    private int eloChange;
    @Singular("log")
    private List<String> log;
    private int rounds;

    public static class BattleReportBuilder {

        private BattleStatus outcome;
        private int eloChange;

        public BattleReportBuilder outcome(BattleStatus outcome) {
            this.outcome = outcome;
            this.eloChange = getEloChange(outcome);
            return this;
        }

        private int getEloChange(BattleStatus outcome) {
            return switch (outcome) {
                case WON -> 3;
                case LOST -> -5;
                default -> 0;
            };
        }
    }
}
