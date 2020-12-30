package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.Table;

import java.time.LocalDateTime;

@Table("token")
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Data
public class TokenEntity {

    @Column
    private final Long id;
    @Column
    private final String token;
    @Column
    private final Long userId;
    @Column
    private final LocalDateTime expiresAt;
}
