package mtcg.model.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ElementTypeTest {

    @ParameterizedTest
    @EnumSource(ElementType.class)
    void getWeaknessEnum_allEnumValues_returnEnums(ElementType elementType) {
        Assertions.assertDoesNotThrow(elementType::getWeaknessEnum);
    }
}