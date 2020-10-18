package mtcg.model.cards;

import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static mtcg.model.CardsFixture.monsterCard;
import static mtcg.model.CardsFixture.spellCard;
import static mtcg.model.enums.Effectiveness.EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NOT_EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NO_EFFECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpellCardAttackingTest {

    SpellCardAttacking currentSpellCard;
    SpellCardAttacking otherSpellCard;
    MonsterCard otherMonsterCard;

    static Stream<Arguments> effectiveCombinations() {
        return Stream.of(
                arguments(EFFECTIVE, true),
                arguments(NO_EFFECT, false),
                arguments(NOT_EFFECTIVE, false));
    }

    @BeforeEach
    void setup() {
        currentSpellCard = spy(spellCard());
        otherSpellCard = spy(spellCard());
        otherMonsterCard = spy(monsterCard());
    }

    @ParameterizedTest
    @MethodSource("effectiveCombinations")
    void attack_effectivenessVsSpell(Effectiveness effectiveness, boolean result) {
        doReturn(effectiveness).when(currentSpellCard).getEffectiveMultiplier(any());

        assertEquals(currentSpellCard.attack(otherSpellCard), result);
    }

    @Test
    void attack_damageHigherVsMonster() {
        when(currentSpellCard.getDamage()).thenReturn(10f);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentSpellCard).getEffectiveMultiplier(any());
        when(otherMonsterCard.getDamage()).thenReturn(5f);
        doReturn(NO_EFFECT).when(otherMonsterCard).getEffectiveMultiplier(any());

        assertFalse(currentSpellCard.attack(otherMonsterCard));
        assertTrue(currentSpellCard.attack(otherMonsterCard));
        assertTrue(currentSpellCard.attack(otherMonsterCard));
    }

    @Test
    void attack_damageLowerVsMonster() {
        when(currentSpellCard.getDamage()).thenReturn(5f);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentSpellCard).getEffectiveMultiplier(any());
        when(otherMonsterCard.getDamage()).thenReturn(10f);
        doReturn(NO_EFFECT).when(otherMonsterCard).getEffectiveMultiplier(any());

        assertFalse(currentSpellCard.attack(otherMonsterCard));
        assertFalse(currentSpellCard.attack(otherMonsterCard));
        assertFalse(currentSpellCard.attack(otherMonsterCard));
    }

    @Test
    void attack_vsUnknownType_throwException() {
        assertThrows(UnsupportedOperationException.class,
                () -> currentSpellCard.attack(null),
                "The Card being attacked has an unknown type!");
    }
}