package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Post;
import http.model.annotation.Secured;
import lombok.RequiredArgsConstructor;
import mtcg.model.battle.BattleReport;
import mtcg.model.user.User;
import mtcg.service.battle.MatchmakingService;

@Controller
@RequiredArgsConstructor
@Secured
public class BattleController {

    private final MatchmakingService matchmakingService;

    @Post("/api/battles")
    public BattleReport searchBattle(User user) {
        if (user.getDeck().size() != 4) {
            throw new IllegalStateException("Deck size must be 4");
        }
        return matchmakingService.searchBattle(user);
    }
}
