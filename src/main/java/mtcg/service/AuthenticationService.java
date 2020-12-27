package mtcg.service;

import http.model.annotation.Component;
import http.model.interfaces.Authentication;
import lombok.RequiredArgsConstructor;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.user.User;
import mtcg.service.card.CardGenerator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final CardGenerator cardGenerator;

    private Map<String, String> tokens = new ConcurrentHashMap<>(
            Map.of("ADMIN", "ADMIN",
                    "dGVzdDE6dGVzdA==", "USER",
                    "dGVzdDI6dGVzdA==", "TEST"));

    public Optional<Authentication> authenticateUser(String token) {
        if (!tokens.containsKey(token)) {
            return Optional.empty();
        }
        return Optional.of(User.builder()
                .username(token)
                .role(tokens.get(token))
                .coins(100)
                .deck(cardGenerator.generateCardPackage().open().stream().filter(c -> c instanceof BattleCard).map(c -> (BattleCard)c).collect(Collectors.toList()))
                .build());
    }
}
