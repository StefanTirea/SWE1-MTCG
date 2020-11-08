package mtcg.model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static mtcg.model.fixture.CardsFixture.monsterCard;
import static mtcg.model.fixture.CardsFixture.spellCard;
import static mtcg.model.enums.Effectiveness.EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NOT_EFFECTIVE;
import static mtcg.model.enums.Effectiveness.NO_EFFECT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void attack_damageHigherVsMonster_returnsTrue() {
        when(currentMonsterCard.getDamage()).thenReturn(10f);
        when(otherMonsterCard.getDamage()).thenReturn(5f);

        assertTrue(currentMonsterCard.attack(otherMonsterCard));
    }

    @Test
    void attack_damageLowerVsMonster_returnsFalse() {
        when(currentMonsterCard.getDamage()).thenReturn(5f);
        when(otherMonsterCard.getDamage()).thenReturn(10f);

        assertFalse(currentMonsterCard.attack(otherMonsterCard));
    }

    @Test
    void attack_damageSameVsMonster_returnsFalse() {
        when(currentMonsterCard.getDamage()).thenReturn(5f);
        when(otherMonsterCard.getDamage()).thenReturn(5f);

        assertFalse(currentMonsterCard.attack(otherMonsterCard));
    }

    @Test
    void attack_damageHigherVsSpell() {
        when(currentMonsterCard.getDamage()).thenReturn(10f);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentMonsterCard).getEffectiveMultiplier(any());
        when(otherSpellCard.getDamage()).thenReturn(5f);
        doReturn(NO_EFFECT).when(otherSpellCard).getEffectiveMultiplier(any());

        assertFalse(currentMonsterCard.attack(otherSpellCard));
        assertTrue(currentMonsterCard.attack(otherSpellCard));
        assertTrue(currentMonsterCard.attack(otherSpellCard));
    }

    @Test
    void attack_damageLowerVsSpell() {
        when(currentMonsterCard.getDamage()).thenReturn(5f);
        doReturn(NOT_EFFECTIVE, NO_EFFECT, EFFECTIVE).when(currentMonsterCard).getEffectiveMultiplier(any());
        when(otherSpellCard.getDamage()).thenReturn(10f);
        doReturn(NO_EFFECT).when(otherSpellCard).getEffectiveMultiplier(any());

        assertFalse(currentMonsterCard.attack(otherSpellCard));
        assertFalse(currentMonsterCard.attack(otherSpellCard));
        assertFalse(currentMonsterCard.attack(otherSpellCard));
    }

    @Test
    void attack_vsUnknownType_throwException() {
        assertThrows(UnsupportedOperationException.class,
                () -> currentMonsterCard.attack(null),
                "The Card being attacked has an unknown type!");
    }
}
