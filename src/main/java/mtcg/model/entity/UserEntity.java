package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.IgnoreUpdate;
import mtcg.model.entity.annotation.Table;

@Builder
@Table("user")
@RequiredArgsConstructor
@Data
public class UserEntity {

    @Column
    @IgnoreUpdate
    private final Long id;
    @Column
    @IgnoreUpdate
    private final String username;
    @Column
    @IgnoreUpdate
    private final String password;
    @Column
    @IgnoreUpdate
    private final String role;
    @Column
    private final int coins;
    @Column
    private final Long[] deck;
    @Column
    private final int elo;
    @Column
    private final int gamesPlayed;
    @Column
    private final int gamesWon;

    @Builder
    public UserEntity(String username, String password) {
        this.id = null;
        this.username = username;
        this.password = password;
        this.role = "USER";
        this.coins = 20;
        this.deck = new Long[0];
        this.elo = 100;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }
}
