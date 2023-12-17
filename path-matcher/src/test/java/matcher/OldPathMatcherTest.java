package matcher;

import java.util.Map;
import java.util.stream.Stream;
import matcher.PathMatchTestSuite.PathMatcherMatchedPathVariableTest;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static matcher.PathMatchTestSuite.PathMatchGetValueTest;

class OldPathMatcherTest {
    @DisplayName("matcher 에 대해 path 가 매칭되면, 존재하는 값을 반환합니다.")
    @PathMatchGetValueTest
    void Given_BasePahAndRequestPath_When_Matched_Then_GetPresentValue(String basePath, String requestPath, boolean expect) throws Exception {
        // given
        PathUrl basePathUrl = PathUrl.of(basePath);
        PathUrl requestPathUrl = PathUrl.of(requestPath);

        OldPathMatcher oldPathMatcher = OldPathMatcher.of(basePathUrl);

        // when
        boolean actual = oldPathMatcher.match(requestPathUrl).isPresent();

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("matcher 에 대해 path 가 매칭되면, pathVariable 값을 반환합니다.")
    @PathMatcherMatchedPathVariableTest
    void Given_BasePathAndRequestPath_When_Matched_Then_GetPathVariable(String basePath, String requestPath, Map<String, String> expectMap) throws Exception {
        // given
        PathUrl basePathUrl = PathUrl.of(basePath);
        PathUrl requestPathUrl = PathUrl.of(requestPath);
        PathVariable expect = new PathVariable(expectMap);

        OldPathMatcher oldPathMatcher = OldPathMatcher.of(basePathUrl);

        // when
        PathVariable actual = oldPathMatcher.match(requestPathUrl)
            .orElseThrow(() -> new RuntimeException("Does not exist pathVariable."));

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }
}