package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.IgnoreUpdate;
import mtcg.model.entity.annotation.Table;
import mtcg.model.enums.MonsterType;

import java.util.Arrays;

@Table("trading")
@Builder
@Data
@RequiredArgsConstructor
public class TradingEntity {

    @Column
    @IgnoreUpdate
    private final Long id;
    @Column
    private final Long userId;
    @Column
    private final Long cardId;
    @Column
    private final Integer minDamage;
    @Column
    private final String type;

    public boolean isMonsterCard() {
        return Arrays.stream(MonsterType.values())
                .anyMatch(monsterType -> monsterType.name().equalsIgnoreCase(type));
    }
}
