package matcher.segment;

import java.util.Map;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import static matcher.PathMatchTestSuite.TestSuiteSegmentChunkChainConsumePathVariableResult;
import static matcher.PathMatchTestSuite.TestSuiteSegmentChunkChainConsumeResult;
import static matcher.PathMatchTestSuite.TestSuiteSegmentChunkChainCreate;
import static matcher.segment.SegmentChunkChain.of;

class SegmentChunkChainTest {

    @DisplayName("pathurl 로 부터 chain 을 생성합니다.")
    @TestSuiteSegmentChunkChainCreate
    void Given_pathUrl_When_of_Then_create_SegmentChunkChain(String baseUrl) throws Exception {
        //given
        PathUrl pathUrl = PathUrl.of(baseUrl);

        //when
        Throwable actual = Assertions.catchThrowable(() -> of(pathUrl));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("consume 한 url 의 일치여부에 따라 결과값을 반환합니다.")
    @TestSuiteSegmentChunkChainConsumeResult
    void Given_chain_When_consume_Then_matchResult(String chainUrl, String baseRequestUrl, boolean expect) throws Exception {
        //given
        SegmentChunkChain chain = of(PathUrl.of(chainUrl));
        PathUrl requestUrl = PathUrl.of(baseRequestUrl);

        //when
        boolean actual = chain.consume(requestUrl).isAllConsumed();

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("consume 한 url 으로부터 path variable 을 추출합니다.")
    @TestSuiteSegmentChunkChainConsumePathVariableResult
    void Given_chain_When_consume_Then_extract_pathVariable(String chainUrl, String baseRequestUrl, Map<String, String> expect) throws Exception {
        //given
        SegmentChunkChain chain = of(PathUrl.of(chainUrl));
        PathUrl requestUrl = PathUrl.of(baseRequestUrl);
        PathVariable expectPathVariable = new PathVariable(expect);

        //when
        PathVariable actual = chain.consume(requestUrl).getPathVariable();

        //then
        Assertions.assertThat(actual).isEqualTo(expectPathVariable);
    }
}