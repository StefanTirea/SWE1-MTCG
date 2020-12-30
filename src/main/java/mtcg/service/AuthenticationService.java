package mtcg.service;

import http.model.annotation.Component;
import http.model.interfaces.Authentication;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.UserEntity;
import mtcg.model.interfaces.Item;
import mtcg.model.user.User;
import mtcg.persistence.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final ItemService itemService;

    public Optional<Authentication> authenticateUser(String token) {
        Optional<UserEntity> user = userRepository.getUserByToken(token);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        List<Item> items = itemService.getInventoryByUser(user.get().getId());
        return Optional.of(User.builder()
                .id(user.get().getId())
                .username(user.get().getUsername())
                .role(user.get().getRole())
                .inventory(items)
                .coins(user.get().getCoins())
                .elo(user.get().getElo())
                .gamesPlayed(user.get().getGamesPlayed())
                .gamesWon(user.get().getGamesWon())
                .deck(itemService.getDeck(Arrays.asList(user.get().getDeck()), items))
                .build());
    }
}
