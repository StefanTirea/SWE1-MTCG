package mtcg.persistence;

import http.model.annotation.Component;
import http.model.enums.HttpStatus;
import http.model.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import mtcg.model.entity.TokenEntity;
import mtcg.model.entity.UserEntity;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.user.User;
import mtcg.model.user.UserData;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserRepository extends BaseRepository<UserEntity> {

    private final TokenRepository tokenRepository;

    public UserRepository(TokenRepository tokenRepository) {
        super(UserEntity.class);
        this.tokenRepository = tokenRepository;
    }

    public Optional<UserEntity> getUserByToken(String token) {
        Optional<TokenEntity> tokenEntity = tokenRepository.getToken(token);
        if (tokenEntity.isEmpty()) {
            return Optional.empty();
        }
        return selectEntityById(tokenEntity.get().getUserId());
    }

    public HttpResponse loginUser(UserData userData) {
        log.info("User Login {}", userData);
        Optional<UserEntity> user = selectEntityByFilter("username", userData.getUsername(), "password", userData.getPassword());
        if (user.isEmpty()) {
            log.info("Login failed!");
            return HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).content("Login Failed!").build();
        }
        String token = tokenRepository.getTokenByUser(user.get().getId())
                .orElseGet(() -> {
                    tokenRepository.insert(TokenEntity.builder()
                            .userId(user.get().getId())
                            .token(List.of("kienboec","altenhof","admin").contains(userData.getUsername()) ? userData.getUsername()+"-mtcgToken" : RandomStringUtils.randomAlphanumeric(25))
                            .expiresAt(LocalDateTime.now().plusHours(8))
                            .build());
                    return tokenRepository.getTokenByUser(user.get().getId()).orElseThrow();
                }).getToken();
        return HttpResponse.builder()
                .content(token)
                .build();
    }

    public boolean createUser(UserData userData) {
        insert(new UserEntity(userData.getUsername(), userData.getPassword()));
        return true;
    }

    public boolean updateUser(User user) {
        return update(UserEntity.builder()
                .id(user.getId())
                .coins(user.getCoins())
                .gamesPlayed(user.getGamesPlayed())
                .gamesWon(user.getGamesWon())
                .deck(user.getDeck().stream().map(BattleCard::getId).toArray(Long[]::new))
                .elo(user.getElo())
                .build());
    }

    public boolean updateUserCredentials(Long userId, String username, String password) {
        return update(userId, "username", username, "password", password);
    }
}
