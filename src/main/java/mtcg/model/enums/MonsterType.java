package mtcg.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MonsterType {

    GOBLIN("Goblin"),
    DRAGON("Dragon"),
    WIZZARD("Wizzard"),
    KNIGHT("Knight"),
    KRAKEN("Kraken"),
    ELV("Elv"),
    ORC("Orc"),
    CHICKEN("Chicken");

    private final String text;
}
