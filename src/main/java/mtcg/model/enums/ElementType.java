package mtcg.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ElementType {
    FIRE("Fire", "WATER"), WATER("Water", "DIRT");

    private String text;
    private String weakness;

    public ElementType getWeaknessEnum() {
        return ElementType.valueOf(this.getWeakness());
    }
}
