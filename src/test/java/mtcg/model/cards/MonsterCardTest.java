package mtcg.model.cards;

import mtcg.model.enums.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static mtcg.model.enums.Effectiveness.EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NOT_EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NO_EFFECT;
import static mtcg.model.fixture.CardsFixture.monsterCard;
import static mtcg.model.fixture.CardsFixture.spellCard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonsterCardTest {

    MonsterCard currentMonsterCard;
    MonsterCard otherMonsterCard;
    SpellCardAttacking otherSpellCard;

    @BeforeEach
    void setup() {
        currentMonsterCard = spy(monsterCard());
        otherMonsterCard = spy(monsterCard());
        otherSpellCard = spy(spellCard());
    }

    @Test
    void attack_damageHigherVsMonster_returnsAttacker() {
        when(currentMonsterCard.getDamage()).thenReturn(10);
        when(otherMonsterCard.getDamage()).thenReturn(5);

        assertThat(currentMonsterCard.attack(otherMonsterCard)).isEqualTo(RuleResult.ATTACKER);
    }

    @Test
    void attack_damageLowerVsMonster_returnsDefender() {
        when(currentMonsterCard.getDamage()).thenReturn(5);
        when(otherMonsterCard.getDamage()).thenReturn(10);

        assertThat(currentMonsterCard.attack(otherMonsterCard)).isEqualTo(RuleResult.DEFENDER);
    }

    @Test
    void attack_damageSameVsMonster_returnsNothing() {
        when(currentMonsterCard.getDamage()).thenReturn(5);
        when(otherMonsterCard.getDamage()).thenReturn(5);

        assertThat(currentMonsterCard.attack(otherMonsterCard)).isEqualTo(RuleResult.NOTHING);
    }

    @Test
    void attack_damageHigherVsSpell() {
        when(currentMonsterCard.getDamage()).thenReturn(10);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentMonsterCard).getEffectiveMultiplier(any());
        when(otherSpellCard.getDamage()).thenReturn(5);
        doReturn(NO_EFFECT).when(otherSpellCard).getEffectiveMultiplier(any());

        assertThat(currentMonsterCard.attack(otherSpellCard)).isEqualTo(RuleResult.NOTHING);
        assertThat(currentMonsterCard.attack(otherSpellCard)).isEqualTo(RuleResult.ATTACKER);
        assertThat(currentMonsterCard.attack(otherSpellCard)).isEqualTo(RuleResult.ATTACKER);
    }

    @Test
    void attack_damageLowerVsSpell() {
        when(currentMonsterCard.getDamage()).thenReturn(5);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentMonsterCard).getEffectiveMultiplier(any());
        when(otherSpellCard.getDamage()).thenReturn(10);
        doReturn(NO_EFFECT).when(otherSpellCard).getEffectiveMultiplier(any());

        assertThat(currentMonsterCard.attack(otherSpellCard)).isEqualTo(RuleResult.DEFENDER);
        assertThat(currentMonsterCard.attack(otherSpellCard)).isEqualTo(RuleResult.DEFENDER);
        assertThat(currentMonsterCard.attack(otherSpellCard)).isEqualTo(RuleResult.DEFENDER);
    }
}
