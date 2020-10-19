package mtcg.model.cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;
import mtcg.model.interfaces.BattleCard;

@Data
@AllArgsConstructor
public abstract class BasicBattleCard implements BattleCard {

    private final String name;
    private final int mana;
    private final float damage;
    @NonNull
    private final ElementType elementType;

    @Override
    public Effectiveness getEffectiveMultiplier(ElementType elementType) {
        if (getElementType().equals(elementType)) {
            return Effectiveness.NO_EFFECT;
        } else if (elementType.getWeaknessEnum().equals(getElementType())) {
            return Effectiveness.EFFECTIVE;
        } else {
            return Effectiveness.NOT_EFFECTIVE;
        }
    }

    @Override
    public float getDamageWithEffectiveMultiplier(BattleCard card, ElementType enemyElementType) {
        return card.getDamage() * card.getEffectiveMultiplier(enemyElementType).getPercentage();
    }

    @Override
    public Boolean attack(BattleCard otherCard) {
        return null;
    }
}
