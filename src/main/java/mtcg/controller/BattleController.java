package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Get;
import http.model.annotation.Secured;
import lombok.RequiredArgsConstructor;
import mtcg.model.BattleReport;
import mtcg.model.user.User;
import mtcg.service.battle.MatchmakingService;

@Controller
@RequiredArgsConstructor
@Secured
public class BattleController {

    private final MatchmakingService matchmakingService;

    @Get("/api/battles")
    public BattleReport searchBattle(User user) {
        if (user.getDeck().size() <= 3) {
            throw new IllegalStateException("Deck size must be 5");
        }
        return matchmakingService.searchBattle(user);
    }
}
