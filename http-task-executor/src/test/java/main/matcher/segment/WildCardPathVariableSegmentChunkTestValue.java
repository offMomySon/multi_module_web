package main.matcher.segment;

import matcher.segment.PathUrl2;
import matcher.segment.PathVariableValue;
import matcher.segment.WildCardPathVariableSegmentChunk;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WildCardPathVariableSegmentChunkTestValue {
    @DisplayName("첫번쨰 segment 가 wildcard 가 아니면 exception 이 발생합니다.")
    @Test
    void ttttttest() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("/pv1/pv2");

        //when
        Throwable actual = Assertions.catchThrowable(() -> new WildCardPathVariableSegmentChunk(requestUrl));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("pathVaraible 이 존재하지 않으면, exception 이 발생합니다.")
    @Test
    void tttttttest() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("**/pv1/pv2");

        //when
        Throwable actual = Assertions.catchThrowable(() -> new WildCardPathVariableSegmentChunk(requestUrl));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("일치하지 않으면 빈값을 반환합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("/path1/pv1/diffPath2");
        PathUrl2 baseUrl = PathUrl2.from("**/path1/{pv1}/path2");
        WildCardPathVariableSegmentChunk wildCardPathVariableSegmentChunk = new WildCardPathVariableSegmentChunk(baseUrl);

        //when
        List<PathUrl2> actual = wildCardPathVariableSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actual).hasSize(0);
    }

    @DisplayName("일치하고 남은 requestUrl 을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideConsumeResult")
    void ttest(String _baseUrl, String _requestUrl, List<PathUrl2> expect) throws Exception {
        //given
        PathUrl2 baseUrl = PathUrl2.from(_baseUrl);
        WildCardPathVariableSegmentChunk wildCardPathVariableSegmentChunk = new WildCardPathVariableSegmentChunk(baseUrl);
        PathUrl2 requestUrl = PathUrl2.from(_requestUrl);

        //when
        List<PathUrl2> actuals = wildCardPathVariableSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actuals).hasSize(expect.size());
        Assertions.assertThat(actuals).containsAll(expect);
    }

    @DisplayName("pathVaraible 에 매칭된 segment 들을 반환합니다.")
    @ParameterizedTest
    @MethodSource("providePathUrlPathVariable")
    void ttttest(String _baseUrl, String _requestUrl, Map<PathUrl2, PathVariableValue> expects) throws Exception {
        //given
        PathUrl2 baseUrl = PathUrl2.from(_baseUrl);
        WildCardPathVariableSegmentChunk wildCardPathVariableSegmentChunk = new WildCardPathVariableSegmentChunk(baseUrl);

        PathUrl2 requestUrl = PathUrl2.from(_requestUrl);

        //when
        Map<PathUrl2, PathVariableValue> actuals = wildCardPathVariableSegmentChunk.internalConsume(requestUrl);

        //then
        Assertions.assertThat(actuals).isEqualTo(expects);
    }

    public static Stream<Arguments> provideConsumeResult() {
        return Stream.of(
            Arguments.of("**/{pv1}", "path1/path2/path3", List.of(
                new PathUrl2(new StringBuilder("path1/path2/path3"), "path1/".length()),
                new PathUrl2(new StringBuilder("path1/path2/path3"), "path1/path2/".length()),
                new PathUrl2(new StringBuilder("path1/path2/path3"), "path1/path2/path3".length())
            )),
            Arguments.of("**/{pv1}/path1", "path1/path1/path1/path2/path1/path3", List.of(
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/".length()),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/".length()),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/path1/".length())
            )),
            Arguments.of("**/path1/{pv1}", "path1/path1/path1/path2/path1/path3", List.of(
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/".length()),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/".length()),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/".length()),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/path1/path3".length())
            )),
            Arguments.of("**/path1/{pv1}/path2", "path1/path1/path1/path2/path1/path3", List.of(
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/".length())
            ))
        );
    }

    public static Stream<Arguments> providePathUrlPathVariable() {
        return Stream.of(
            Arguments.of("**/{pv1}", "path1/path2/path3", Map.of(
                new PathUrl2(new StringBuilder("path1/path2/path3"), "path1/".length()), new PathVariableValue(Map.of("pv1", "path1")),
                new PathUrl2(new StringBuilder("path1/path2/path3"), "path1/path2/".length()), new PathVariableValue(Map.of("pv1", "path2")),
                new PathUrl2(new StringBuilder("path1/path2/path3"), "path1/path2/path3".length()), new PathVariableValue(Map.of("pv1", "path3"))
            )),

            Arguments.of("**/{pv1}/path1", "path1/path1/path1/path2/path1/path3", Map.of(
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/".length()), new PathVariableValue(Map.of("pv1", "path1")),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/".length()), new PathVariableValue(Map.of("pv1", "path1")),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/path1/".length()), new PathVariableValue(Map.of("pv1", "path2"))
            )),
            Arguments.of("**/path1/{pv1}", "path1/path1/path1/path2/path1/path3", Map.of(
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/".length()), new PathVariableValue(Map.of("pv1", "path1")),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/".length()), new PathVariableValue(Map.of("pv1", "path1")),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/".length()), new PathVariableValue(Map.of("pv1", "path2")),
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/path1/path3".length()), new PathVariableValue(Map.of("pv1", "path3"))
            )),
            Arguments.of("**/path1/{pv1}/path2", "path1/path1/path1/path2/path1/path3", Map.of(
                new PathUrl2(new StringBuilder("path1/path1/path1/path2/path1/path3"), "path1/path1/path1/path2/".length()), new PathVariableValue(Map.of("pv1", "path1"))
            ))
        );
    }
}