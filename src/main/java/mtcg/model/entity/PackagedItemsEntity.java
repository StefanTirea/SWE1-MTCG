package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.Table;

@Table("packaged_items")
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Data
public class PackagedItemsEntity {

    @Column
    private final Long id;
    @Column
    private final Long packageId;
    @Column
    private final Long cardId;
}
