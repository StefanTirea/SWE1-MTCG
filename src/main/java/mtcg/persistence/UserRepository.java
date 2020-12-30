package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.entity.TokenEntity;
import mtcg.model.entity.UserEntity;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.user.User;
import mtcg.model.user.UserData;
import mtcg.persistence.base.BaseRepository;
import mtcg.persistence.base.ConnectionPool;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class UserRepository extends BaseRepository<UserEntity> {

    private final TokenRepository tokenRepository;

    public UserRepository(ConnectionPool connectionPool, TokenRepository tokenRepository) {
        super(connectionPool, UserEntity.class);
        this.tokenRepository = tokenRepository;
    }

    public Optional<UserEntity> getUserByToken(String token) {
        Optional<TokenEntity> tokenEntity = tokenRepository.getToken(token);
        if (tokenEntity.isEmpty()) {
            return Optional.empty();
        }
        return getEntityById(tokenEntity.get().getUserId());
    }

    public Optional<String> loginUser(UserData userData) {
        Optional<UserEntity> user = getEntityByFilter("username", userData.getUsername(), "password", userData.getPassword());
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tokenRepository.getTokenByUser(user.get().getId())
                .orElseGet(() -> {
                    tokenRepository.insert(TokenEntity.builder()
                            .userId(user.get().getId())
                            .token(RandomStringUtils.randomAlphanumeric(20))
                            .expiresAt(LocalDateTime.now().plusHours(8))
                            .build());
                    return tokenRepository.getTokenByUser(user.get().getId()).orElseThrow();
                }).getToken());
    }

    public boolean createUser(UserData userData) {
        return insert(UserEntity.builder()
                .username(userData.getUsername())
                .password(userData.getPassword())
                .build());
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
}
