package mtcg.service;

import mtcg.model.cards.MonsterCard;
import mtcg.model.cards.SpellCardAttacking;
import mtcg.model.enums.ElementType;
import mtcg.model.enums.MonsterType;
import mtcg.model.enums.RuleResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

public class CardRules {

    /**
     * @param attacker one of the two battling Monster Cards
     * @param defender one of the two battling Monster Cards
     * @return result of the rule {@link RuleResult} independent from the position of the monster parameter
     */
    public static RuleResult checkRulesMonsterVsMonster(MonsterCard attacker, MonsterCard defender) {
        List<RuleResult> battleResult = Stream.of(
                checkRulesMonsterVsMonster(attacker, defender, MonsterType.GOBLIN, null, MonsterType.DRAGON, null),
                checkRulesMonsterVsMonster(attacker, defender, MonsterType.ORC, null, MonsterType.WIZZARD, null),
                checkRulesMonsterVsMonster(attacker, defender, MonsterType.DRAGON, null, MonsterType.ELF, ElementType.FIRE))
                .filter(result -> !result.equals(RuleResult.NOTHING))
                .collect(Collectors.toList());
        assert battleResult.size() <= 1 : String.format("The Monster vs Monster Rules does not return a clear result ATTACK: %s ; DEFENDER: %s", attacker, defender);
        return battleResult.isEmpty() ? RuleResult.NOTHING : battleResult.get(0);
    }

    /**
     *
     * @param monster battling monster card
     * @param spell battling spell card
     * @param spellAttacking is true when the spell card is attacking
     * @return result of the rule {@link RuleResult} dependent from value of {@code spellAttacking}
     */
    public static RuleResult checkRulesMonsterVsSpell(MonsterCard monster, SpellCardAttacking spell, boolean spellAttacking) {
        List<RuleResult> battleResult = Stream.of(
                ruleMonsterVsSpellWithSpellSuperior(monster, spell, MonsterType.KNIGHT, null, ElementType.WATER, spellAttacking),
                ruleMonsterVsSpellWithMonsterSuperior(monster, spell, MonsterType.KRAKEN, null, null, !spellAttacking))
                .filter(result -> !result.equals(RuleResult.NOTHING))
                .collect(Collectors.toList());
        assert battleResult.size() <= 1 : String.format("The Monster vs Spell Rules does not return a clear result ATTACK: %s ; DEFENDER: %s", monster, spell);
        return battleResult.isEmpty() ? RuleResult.NOTHING : battleResult.get(0);
    }

    private static RuleResult checkRulesMonsterVsMonster(MonsterCard attacker, MonsterCard defender,
                                                         MonsterType weaknessMonster, ElementType weaknessElement,
                                                         MonsterType superiorMonster, ElementType superiorElement) {
        if ((attacker.getMonsterType().equals(weaknessMonster) || isNull(weaknessMonster))
                && (attacker.getElementType().equals(weaknessElement) || isNull(weaknessElement))
                && (defender.getMonsterType().equals(superiorMonster) || isNull(superiorMonster))
                && (defender.getElementType().equals(superiorElement) || isNull(superiorElement))) {
            return RuleResult.DEFENDER;
        } else if ((attacker.getMonsterType().equals(superiorMonster) || isNull(superiorMonster))
                && (attacker.getElementType().equals(superiorElement) || isNull(superiorElement))
                && (defender.getMonsterType().equals(weaknessMonster) || isNull(weaknessMonster))
                && (defender.getElementType().equals(weaknessElement) || isNull(weaknessElement))) {
            return RuleResult.ATTACKER;
        } else {
            return RuleResult.NOTHING;
        }
    }

    private static RuleResult ruleMonsterVsSpellWithSpellSuperior(MonsterCard monster, SpellCardAttacking spell,
                                                                  MonsterType weaknessMonster, ElementType weaknessElement,
                                                                  ElementType superiorElement, boolean spellAttacking) {
        if ((monster.getMonsterType().equals(weaknessMonster) || isNull(weaknessMonster))
                && (monster.getElementType().equals(weaknessElement) || isNull(weaknessElement))
                && (spell.getElementType().equals(superiorElement) || isNull(superiorElement))) {
            return spellAttacking ? RuleResult.ATTACKER : RuleResult.DEFENDER;
        } else {
            return RuleResult.NOTHING;
        }
    }

    private static RuleResult ruleMonsterVsSpellWithMonsterSuperior(MonsterCard monster, SpellCardAttacking spell,
                                                                  MonsterType superiorMonster, ElementType superiorElement,
                                                                  ElementType weaknessElement, boolean monsterAttacking) {
        if ((monster.getMonsterType().equals(superiorMonster) || isNull(superiorMonster))
                && (monster.getElementType().equals(superiorElement) || isNull(superiorElement))
                && (spell.getElementType().equals(weaknessElement) || isNull(weaknessElement))) {
            return monsterAttacking ? RuleResult.ATTACKER : RuleResult.DEFENDER;
        } else {
            return RuleResult.NOTHING;
        }
    }
}
