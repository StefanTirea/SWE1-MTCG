package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Get;
import http.model.annotation.Secured;
import lombok.RequiredArgsConstructor;
import mtcg.model.battle.BattleStats;
import mtcg.model.user.User;
import mtcg.service.StatsService;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Get("/api/users/stats")
    @Secured
    public BattleStats getUserStats(User user) {
        return statsService.getUserStats(user);
    }

    @Get("/api/users/elo")
    public List<Map<String, Integer>> getEloStats() {
        return statsService.getEloStats();
    }
}
