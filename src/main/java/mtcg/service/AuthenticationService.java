package mtcg.service;

import http.model.annotation.Component;
import http.model.interfaces.Authentication;
import mtcg.model.user.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthenticationService {

    private ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>(
            Map.of("ADMIN", "ADMIN",
                    "USER", "USER",
                    "TEST", "TEST"));

    public Optional<Authentication> authenticateUser(String token) {
        if (!tokens.containsKey(token)) {
            return Optional.empty();
        }
        return Optional.of(User.builder()
                .username(token)
                .role(tokens.get(token))
                .coins(100)
                .build());
    }
}
