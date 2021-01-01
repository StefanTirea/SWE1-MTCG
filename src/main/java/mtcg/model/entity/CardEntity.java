package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.IgnoreUpdate;
import mtcg.model.entity.annotation.Table;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;

@Table("card")
@Builder
@Data
@RequiredArgsConstructor
public class CardEntity {

    @Column
    @IgnoreUpdate
    private final Long id;
    @Column
    private final Long userId;
    @Column
    @IgnoreUpdate
    private final String name;
    @Column
    @IgnoreUpdate
    private final MonsterType monsterType;
    @Column
    @IgnoreUpdate
    private final ElementType elementType;
    @Column
    @IgnoreUpdate
    private final int damage;
    @Column
    private final boolean locked;

    public String getMonsterType() {
        return monsterType != null ? monsterType.name() : null;
    }

    public String getElementType() {
        return elementType.name();
    }

    public boolean getLocked() {
        return locked;
    }
}
