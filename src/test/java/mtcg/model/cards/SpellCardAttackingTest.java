package mtcg.model.cards;

import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static mtcg.model.enums.Effectiveness.EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NOT_EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NO_EFFECT;
import static mtcg.model.enums.RuleResult.ATTACKER;
import static mtcg.model.enums.RuleResult.DEFENDER;
import static mtcg.model.enums.RuleResult.NOTHING;
import static mtcg.model.fixture.CardsFixture.monsterCard;
import static mtcg.model.fixture.CardsFixture.spellCard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
                arguments(EFFECTIVE, ATTACKER),
                arguments(NO_EFFECT, NOTHING),
                arguments(NOT_EFFECTIVE, DEFENDER));
    }

    @BeforeEach
    void setup() {
        currentSpellCard = spy(spellCard());
        otherSpellCard = spy(spellCard());
        otherMonsterCard = spy(monsterCard());
    }

    @ParameterizedTest
    @MethodSource("effectiveCombinations")
    void attack_effectivenessVsSpell(Effectiveness effectiveness, RuleResult result) {
        doReturn(effectiveness).when(currentSpellCard).getEffectiveMultiplier(any());

        assertEquals(currentSpellCard.attack(otherSpellCard), result);
    }

    @Test
    void attack_damageHigherVsMonster() {
        when(currentSpellCard.getDamage()).thenReturn(10);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentSpellCard).getEffectiveMultiplier(any());
        when(otherMonsterCard.getDamage()).thenReturn(5);
        doReturn(NO_EFFECT).when(otherMonsterCard).getEffectiveMultiplier(any());

        assertThat(currentSpellCard.attack(otherMonsterCard)).isEqualTo(NOTHING);
        assertThat(currentSpellCard.attack(otherMonsterCard)).isEqualTo(ATTACKER);
        assertThat(currentSpellCard.attack(otherMonsterCard)).isEqualTo(ATTACKER);
    }

    @Test
    void attack_damageLowerVsMonster() {
        when(currentSpellCard.getDamage()).thenReturn(5);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentSpellCard).getEffectiveMultiplier(any());
        when(otherMonsterCard.getDamage()).thenReturn(10);
        doReturn(NO_EFFECT).when(otherMonsterCard).getEffectiveMultiplier(any());

        assertThat(currentSpellCard.attack(otherMonsterCard)).isEqualTo(DEFENDER);
        assertThat(currentSpellCard.attack(otherMonsterCard)).isEqualTo(DEFENDER);
        assertThat(currentSpellCard.attack(otherMonsterCard)).isEqualTo(DEFENDER);
    }
}
