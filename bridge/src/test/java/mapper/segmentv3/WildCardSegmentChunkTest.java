package mapper.segmentv3;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WildCardSegmentChunkTest {
    @DisplayName("첫번쨰 segment 가 wildcard 가 아니면 exception 이 발생합니다.")
    @Test
    void ttttttest() throws Exception {
        //given
        PathUrl requestUrl = PathUrl.from("/pv1/pv2");

        //when
        Throwable actual = Assertions.catchThrowable(() -> new WildCardSegmentChunk(requestUrl));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("일치하지 않으면 빈값을 반환합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl requestUrl = PathUrl.from("/pv1/pv2/diff");
        PathUrl baseUrl = PathUrl.from("**/pv1/pv2/pv3");
        WildCardSegmentChunk wildCardSegmentChunk = new WildCardSegmentChunk(baseUrl);

        //when
        List<PathUrl> actual = wildCardSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actual).hasSize(0);
    }

    @DisplayName("일치하고 남은 requestUrl 을 반환합니다.")
    @Test
    void ttest() throws Exception {
        //given
        PathUrl requestUrl = PathUrl.from("pv1/pv1/pv2/pv1/pv3");
        PathUrl baseUrl = PathUrl.from("**/pv1");
        WildCardSegmentChunk wildCardSegmentChunk = new WildCardSegmentChunk(baseUrl);

        List<PathUrl> expects =
            List.of(
                new PathUrl(new StringBuilder("pv1/pv1/pv2/pv1/pv3"), "pv1/".length()),
                new PathUrl(new StringBuilder("pv1/pv1/pv2/pv1/pv3"), "pv1/pv1/".length()),
                new PathUrl(new StringBuilder("pv1/pv1/pv2/pv1/pv3"), "pv1/pv1/pv2/pv1/".length())
            );

        //when
        List<PathUrl> actuals = wildCardSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actuals).hasSize(3);
        Assertions.assertThat(actuals).containsSequence(expects);
    }
}