package mtcg.model.interfaces;

import mtcg.model.enums.RuleResult;

public interface Attackable {

    RuleResult attack(BattleCard otherCard);
}
