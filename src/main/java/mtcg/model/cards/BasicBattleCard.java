package mtcg.model.cards;

import mtcg.model.enums.ElementType;
import mtcg.model.interfaces.BattleCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public abstract class BasicBattleCard implements BattleCard {

    private final String name;
    private final String description;
    private final int mana;
    private final float damage;
    private final ElementType elementType;
}
