package mtcg.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Effectiveness {
    EFFECTIVE(1.5f), NO_EFFECT(1f), NOT_EFFECTIVE(0.5f);

    private final float percentage;
}
