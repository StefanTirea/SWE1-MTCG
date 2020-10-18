package mtcg.service;

import mtcg.model.cards.MonsterCard;
import mtcg.model.cards.SpellCardAttacking;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.enums.RuleResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardRulesTest {

    @Test
    void ruleMonsterVsMonster() {
        MonsterCard attacker = MonsterCard.builder()
                .monsterType(MonsterType.STEFAN)
                .elementType(ElementType.NORMAL)
                .build();
        MonsterCard defender = MonsterCard.builder()
                .monsterType(MonsterType.JOHANNES)
                .elementType(ElementType.NORMAL)
                .build();
        assertEquals(CardRules.checkRulesMonsterVsMonster(attacker, defender), RuleResult.DEFENDER);
    }

    @Test
    void ruleMonsterVsSpell() {
        MonsterCard monster = MonsterCard.builder()
                .monsterType(MonsterType.KRAKEN)
                .elementType(ElementType.WATER)
                .build();
        SpellCardAttacking spell = SpellCardAttacking.builder()
                .elementType(ElementType.FIRE)
                .build();

        MonsterCard monster2 = MonsterCard.builder()
                .monsterType(MonsterType.KNIGHT)
                .elementType(ElementType.FIRE)
                .build();
        SpellCardAttacking spell2 = SpellCardAttacking.builder()
                .elementType(ElementType.WATER)
                .build();
        assertEquals(CardRules.checkRulesMonsterVsSpell(monster, spell, false), RuleResult.ATTACKER);
        assertEquals(CardRules.checkRulesMonsterVsSpell(monster, spell, true), RuleResult.DEFENDER);
        assertEquals(CardRules.checkRulesMonsterVsSpell(monster2, spell2, true), RuleResult.ATTACKER);
        assertEquals(CardRules.checkRulesMonsterVsSpell(monster2, spell2, false), RuleResult.DEFENDER);
    }
}