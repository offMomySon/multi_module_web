package matcher;

import java.util.Map;
import matcher.PathMatchTestSuite.TestSuiteSegmentChunkChainConsumePathVariableResult;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import static matcher.PathMatchTestSuite.TestSuiteSegmentChunkChainConsumeResult;

class OldPathMatcherTest {
    @DisplayName("matcher 에 대해 path 가 매칭되면, 존재하는 값을 반환합니다.")
    @TestSuiteSegmentChunkChainConsumeResult
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
    @TestSuiteSegmentChunkChainConsumePathVariableResult
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