package mtcg.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MonsterType {

    GOBLIN("Goblin", 3, 8),
    DRAGON("Dragon", 20, 30),
    WIZZARD("Wizzard", 10, 25),
    KNIGHT("Knight", 18, 28),
    KRAKEN("Kraken", 8, 22),
    ELF("Elf", 5, 12),
    ORC("Orc", 4, 8),
    CHICKEN("Chicken", 1, 5);

    private final String text;
    private final int minDamage;
    private final int maxDamage;
}
