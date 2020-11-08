package server.service.handler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RequestConverterTest {

    private RequestConverter requestConverter = new RequestConverter();

    static Stream<Arguments> primitiveTypesCombinations() {
        return Stream.of(
                arguments(int.class, "5", 5),
                arguments(Integer.class, "5", 5),
                arguments(long.class, "25167167627434", 25167167627434L),
                arguments(Long.class, "25167167627434", 25167167627434L),
                arguments(float.class, "5.55", 5.55f),
                arguments(Float.class, "5.55", 5.55F),
                arguments(double.class, "5.55", 5.55D),
                arguments(Double.class, "5.55", 5.55D),
                arguments(String.class, "test test", "test test"),
                arguments(boolean.class, "true", true),
                arguments(boolean.class, "false", false),
                arguments(boolean.class, "0", false),
                arguments(Boolean.class, "true", Boolean.TRUE));
    }

    @ParameterizedTest
    @MethodSource("primitiveTypesCombinations")
    void pathVariableConverter_convertAllPrimitiveTypes(Class<?> clazz, String input, Object output) {
        assertThat(requestConverter.getPathVariableConverter().get(clazz).apply(input))
                .isEqualTo(output);
    }
}
