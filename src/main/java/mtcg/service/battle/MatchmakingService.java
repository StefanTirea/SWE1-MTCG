package mtcg.service.battle;

import http.model.annotation.Component;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mtcg.model.battle.BattleReport;
import mtcg.model.enums.BattleStatus;
import mtcg.model.user.User;
import mtcg.persistence.BattleResultRepository;
import mtcg.persistence.UserRepository;
import org.apache.commons.lang3.tuple.Pair;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import static mtcg.model.enums.BattleStatus.WAITING;

@Component
@RequiredArgsConstructor
public class MatchmakingService {

    private final BattleResultRepository battleResultRepository;
    private final UserRepository userRepository;
    private final BattleService battleService;
    private final Queue<Pair<String, String>> queue = new PriorityQueue<>();

    @SneakyThrows
    public BattleReport searchBattle(User user) {
        String battleId;
        synchronized (queue) {
            Pair<String, String> opponent = queue.poll();
            if (opponent == null) {
                battleId = UUID.randomUUID().toString();
                battleService.registerBattleId(battleId, user);
                queue.add(Pair.of(user.getUsername(), battleId));
            } else {
                battleId = opponent.getRight();
                battleService.initBattle(battleId, user);
            }
        }
        BattleStatus battleStatus = WAITING;
        for (int i = 0; i < 100; i++) {
            battleStatus = battleService.getBattleStatus(battleId);
            if (BattleStatus.ENDED.equals(battleStatus)) {
                BattleReport battleReport = battleService.getBattleReport(battleId, user.getUsername());
                saveResults(user, battleReport);
                return battleReport;
            }
            Thread.sleep(100);
        }
        synchronized (queue) {
            queue.poll();
        }
        return BattleReport.builder()
                .outcome(battleStatus)
                .build();
    }

    private void saveResults(User user, BattleReport battleReport) {
        user.updateStats(battleReport);
        userRepository.updateUser(user);
        battleResultRepository.saveBattleReport(battleReport, user.getId());
    }
}
