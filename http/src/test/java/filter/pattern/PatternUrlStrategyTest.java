package filter.pattern;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PatternUrlStrategyTest {

    @DisplayName("baseurl 에 따라 적절한 PatternMatcher 를 생성합니다.")
    @ParameterizedTest
    @MethodSource("provideBaseUrlAndExpectClass")
    void test(String basePath, Class<?> expect) throws Exception {
        //given
        //when
        PatternUrl actual = PatternUrlStrategy.create(basePath);

        //then
        Assertions.assertThat(actual).isInstanceOf(expect);
    }

    public static Stream<Arguments> provideBaseUrlAndExpectClass() {
        return Stream.of(
            Arguments.of("/basic", BasePatternUrl.class),
            Arguments.of("/basic/*", WildCardPathUrl.class),
            Arguments.of("*.text", WildCardFileExtensionUrl.class)
        );
    }

}