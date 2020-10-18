package mtcg.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ElementType {

    FIRE("Fire", "WATER"),
    WATER("Water", "NORMAL"),
    NORMAL("Normal", "FIRE")
    ;

    private final String text;
    private final String weakness;

    public ElementType getWeaknessEnum() {
        return ElementType.valueOf(this.getWeakness());
    }
}
