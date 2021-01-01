package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.Table;
import mtcg.model.enums.BattleStatus;

import java.time.LocalDateTime;

@Table("battle_result")
@Builder
@Data
@RequiredArgsConstructor
public class BattleResultEntity {

    @Column
    private final Long id;
    @Column
    private final Long userId;
    @Column
    private final LocalDateTime time;
    @Column
    private final int rounds;
    @Column
    private final BattleStatus result;
    @Column
    private final int eloChange;

    public String getResult() {
        return result.name();
    }

    public BattleStatus getResultEnum() {
        return result;
    }
}
