package mtcg.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.Table;

@Table("package")
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Data
public class PackageEntity {

    @Column
    private final Long id;
    @Column
    private final Long userId;
    @Column
    private final String description;
}
