package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.Table;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;

@Table("card")
@Builder
@Data
@RequiredArgsConstructor
public class CardEntity {

    @Column
    private final Long id;
    @Column
    private final Long userId;
    @Column
    private final String name;
    @Column
    private final MonsterType monsterType;
    @Column
    private final ElementType elementType;
    @Column
    private final int damage;
    @Column
    private final boolean locked;

}
