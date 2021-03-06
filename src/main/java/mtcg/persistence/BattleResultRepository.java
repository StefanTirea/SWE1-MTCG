package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.battle.BattleReport;
import mtcg.model.entity.BattleResultEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BattleResultRepository extends BaseRepository<BattleResultEntity> {

    public BattleResultRepository() {
        super(BattleResultEntity.class);
    }

    public boolean saveBattleReport(BattleReport battleReport, Long userId) {
        insert(BattleResultEntity.builder()
                .result(battleReport.getOutcome())
                .eloChange(battleReport.getEloChange())
                .rounds(battleReport.getRounds())
                .time(LocalDateTime.now())
                .userId(userId)
                .build());
        return true;
    }

    public List<BattleReport> getLatestGames(Long userId) {
        return selectEntitiesByFilter("user_id", userId, "time >=", LocalDateTime.now().minusDays(5)).stream()
                .map(result -> BattleReport.builder()
                        .eloChange(result.getEloChange())
                        .rounds(result.getRounds())
                        .outcome(result.getResultEnum())
                        .build())
                .collect(Collectors.toList());
    }
}
