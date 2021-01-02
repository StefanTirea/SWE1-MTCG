package mtcg.service;

import http.model.annotation.Component;
import lombok.RequiredArgsConstructor;
import mtcg.model.battle.BattleStats;
import mtcg.model.user.User;
import mtcg.persistence.BattleResultRepository;
import mtcg.persistence.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StatsService {

    private final BattleResultRepository battleResultRepository;
    private final UserRepository userRepository;

    public BattleStats getUserStats(User user) {
        return BattleStats.builder()
                .username(user.getUsername())
                .elo(user.getElo())
                .gamesPlayed(user.getGamesPlayed())
                .winRate(String.format("%.0f%%", (float) (100 * user.getGamesWon()) / user.getGamesPlayed()))
                .gamesHistory(battleResultRepository.getLatestGames(user.getId()))
                .build();
    }

    public List<Map<String, Integer>> getEloStats() {
        return userRepository.getEntitiesByFilter().stream()
                .sorted((user1, user2) -> Integer.compare(user2.getElo(), user1.getElo()))
                .map(user -> Map.of(user.getUsername(), user.getElo()))
                .collect(Collectors.toList());
    }
}
