package matcher.segment;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NormalSegmentChunkTest {

    @DisplayName("일치하지 않으면 빈값을 반환합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl requestUrl = PathUrl.from("/path1/path2");
        PathUrl baseUrl = PathUrl.from("/path1/diffPath2");
        NormalSegmentChunk normalSegmentChunk = new NormalSegmentChunk(baseUrl);

        //when
        List<PathUrl> actual = normalSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actual).hasSize(0);
    }

    @DisplayName("일치하고 남은 requestUrl 을 반환합니다.")
    @Test
    void ttest() throws Exception {
        //given
        PathUrl requestUrl = PathUrl.from("/path1/path2/path3/path4");
        PathUrl baseUrl = PathUrl.from("/path1/path2");
        NormalSegmentChunk normalSegmentChunk = new NormalSegmentChunk(baseUrl);

        PathUrl expectUrl = new PathUrl(new StringBuilder("/path1/path2/path3/path4"), "/path1/path2/".length());

        //when
        List<PathUrl> actuals = normalSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actuals).hasSize(1);
        PathUrl actual = actuals.get(0);
        Assertions.assertThat(actual).isEqualTo(expectUrl);
    }

    @DisplayName("requestUrl 이 baseUrl 보다 적은 길이이면 빈값을 반환합니다.")
    @Test
    void tttest() throws Exception {
        //given
        PathUrl requestUrl = PathUrl.from("/path1");
        PathUrl baseUrl = PathUrl.from("/path1/path2/path3/path4");
        NormalSegmentChunk normalSegmentChunk = new NormalSegmentChunk(baseUrl);

        //when
        List<PathUrl> actuals = normalSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actuals).hasSize(0);
    }
}