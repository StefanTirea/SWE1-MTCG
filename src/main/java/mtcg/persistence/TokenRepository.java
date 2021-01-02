package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.entity.TokenEntity;
import http.service.persistence.ConnectionPool;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TokenRepository extends BaseRepository<TokenEntity> {

    public TokenRepository() {
        super(TokenEntity.class);
    }

    public Optional<TokenEntity> getToken(String token) {
        return getEntityByFilter("token", token, "expires_at >=", LocalDateTime.now());
    }

    public Optional<TokenEntity> getTokenByUser(Long userId) {
        return getEntityByFilter("user_id",userId, "expires_at >=", LocalDateTime.now());
    }
}
