package main.matcher.segment;

import matcher.segment.PathUrl2;
import matcher.segment.WildCardSegmentChunk;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WildCardSegmentChunkTest {
    @DisplayName("첫번쨰 segment 가 wildcard 가 아니면 exception 이 발생합니다.")
    @Test
    void ttttttest() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("/pv1/pv2");

        //when
        Throwable actual = Assertions.catchThrowable(() -> new WildCardSegmentChunk(requestUrl));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("일치하지 않으면 빈값을 반환합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("/pv1/pv2/diff");
        PathUrl2 baseUrl = PathUrl2.from("**/pv1/pv2/pv3");
        WildCardSegmentChunk wildCardSegmentChunk = new WildCardSegmentChunk(baseUrl);

        //when
        List<PathUrl2> actual = wildCardSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actual).hasSize(0);
    }

    @DisplayName("일치하고 남은 requestUrl 을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideWildCardSegmentChunk")
    void ttest(String _baseUrl, String _requestUrl, List<PathUrl2> expects) throws Exception {
        //given
        PathUrl2 baseUrl = PathUrl2.from(_baseUrl);
        WildCardSegmentChunk wildCardSegmentChunk = new WildCardSegmentChunk(baseUrl);
        PathUrl2 requestUrl = PathUrl2.from(_requestUrl);

        //when
        List<PathUrl2> actuals = wildCardSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actuals).hasSize(expects.size());
        Assertions.assertThat(actuals).containsSequence(expects);
    }

    public static Stream<Arguments> provideWildCardSegmentChunk() {
        return Stream.of(
            Arguments.of("**", "pv1/pv2/pv3",
                         List.of(
                             new PathUrl2(new StringBuilder("pv1/pv2/pv3"), "".length()),
                             new PathUrl2(new StringBuilder("pv1/pv2/pv3"), "pv1/".length()),
                             new PathUrl2(new StringBuilder("pv1/pv2/pv3"), "pv1/pv2/".length()),
                             new PathUrl2(new StringBuilder("pv1/pv2/pv3"), "pv1/pv2/pv3".length())
                         )),
            Arguments.of("**/pv1", "pv1/pv1/pv2/pv1/pv3",
                         List.of(
                             new PathUrl2(new StringBuilder("pv1/pv1/pv2/pv1/pv3"), "pv1/".length()),
                             new PathUrl2(new StringBuilder("pv1/pv1/pv2/pv1/pv3"), "pv1/pv1/".length()),
                             new PathUrl2(new StringBuilder("pv1/pv1/pv2/pv1/pv3"), "pv1/pv1/pv2/pv1/".length())
                         ))
        );
    }
}