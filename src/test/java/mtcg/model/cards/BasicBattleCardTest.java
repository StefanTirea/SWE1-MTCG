package mtcg.model.cards;

import mtcg.model.enums.Effectiveness;
import mtcg.model.enums.ElementType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicBattleCardTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    BasicBattleCard basicBattleCard;

    static Stream<Arguments> effectiveCombinations() {
        return Stream.of(
                arguments(ElementType.WATER, ElementType.FIRE),
                arguments(ElementType.FIRE, ElementType.NORMAL),
                arguments(ElementType.NORMAL, ElementType.WATER));
    }

    @ParameterizedTest
    @MethodSource("effectiveCombinations")
    void getEffectiveMultiplier_isEffective(ElementType currentElementType, ElementType enemyElementType) {
        when(basicBattleCard.getElementType()).thenReturn(currentElementType);

        assertThat(basicBattleCard.getEffectiveMultiplier(enemyElementType))
                .isEqualTo(Effectiveness.EFFECTIVE);
    }

    @ParameterizedTest
    @MethodSource("effectiveCombinations")
    void getEffectiveMultiplier_isNotEffective(ElementType enemyElementType, ElementType currentElementType) {
        when(basicBattleCard.getElementType()).thenReturn(currentElementType);

        assertThat(basicBattleCard.getEffectiveMultiplier(enemyElementType))
                .isEqualTo(Effectiveness.NOT_EFFECTIVE);
    }

    @ParameterizedTest
    @EnumSource(value = ElementType.class)
    void getEffectiveMultiplier_isNotEffective(ElementType elementType) {
        when(basicBattleCard.getElementType()).thenReturn(elementType);

        assertThat(basicBattleCard.getEffectiveMultiplier(elementType))
                .isEqualTo(Effectiveness.NO_EFFECT);
    }
}