package pretask.pattern;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PatternMatcherStrategyTest {

    @DisplayName("baseurl 에 따라 적절한 PatternMatcher 를 생성합니다.")
    @ParameterizedTest
    @MethodSource("provideBaseUrlAndExpectClass")
    void test(String basePath, Class<?> expect) throws Exception {
        //given
        //when
        PatternMatcherStrategy patternMatcherStrategy = new PatternMatcherStrategy(basePath);
        PatternMatcher actual = patternMatcherStrategy.create();

        //then
        Assertions.assertThat(actual).isInstanceOf(expect);
    }

    public static Stream<Arguments> provideBaseUrlAndExpectClass() {
        return Stream.of(
            Arguments.of("/basic", BasePatternMatcher.class),
            Arguments.of("/basic/*", WildCardPathMatcher.class),
            Arguments.of("*.text", WildCardFileExtensionMatcher.class)
        );
    }

}