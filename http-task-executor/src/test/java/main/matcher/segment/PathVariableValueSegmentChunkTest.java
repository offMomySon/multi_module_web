package main.matcher.segment;

import matcher.segment.PathUrl2;
import matcher.segment.PathVariableSegmentChunk;
import matcher.segment.PathVariableValue;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathVariableValueSegmentChunkTest {
    @DisplayName("일치하지 않으면 빈값을 반환합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("/path1/diffPath1/diffPath2");
        PathUrl2 baseUrl = PathUrl2.from("/path1/{pv1}/path2");
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(baseUrl);

        //when
        List<PathUrl2> actual = pathVariableSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actual).hasSize(0);
    }

    @DisplayName("일치하고 남은 requestUrl 을 반환합니다.")
    @Test
    void ttest() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("path1/diffPath1/path2");
        PathUrl2 baseUrl = PathUrl2.from("path1/{pv1}");
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(baseUrl);

        PathUrl2 expect = new PathUrl2(new StringBuilder("path1/diffPath1/path2"), "path1/diffPath1/".length());

        //when
        List<PathUrl2> actuals = pathVariableSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actuals).hasSize(1);
        PathUrl2 actual = actuals.get(0);
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("pathVaraible 이면 모든 segment 와 일치합니다.")
    @Test
    void tttttest() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("path1/path2/path3/path4");
        PathUrl2 baseUrl = PathUrl2.from("{pv1}/{pv2}/{pv3}");
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(baseUrl);

        PathUrl2 expect = new PathUrl2(new StringBuilder("path1/path2/path3/path4"), "path1/path2/path3/".length());

        //when
        List<PathUrl2> actuals = pathVariableSegmentChunk.consume(requestUrl);

        //then
        Assertions.assertThat(actuals).hasSize(1);
        PathUrl2 actual = actuals.get(0);
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("pathVaraible 에 일치한 segment 들을 반환합니다.")
    @Test
    void ttttest() throws Exception {
        //given
        PathUrl2 requestUrl = PathUrl2.from("path1/path2/path3/path4");
        PathUrl2 baseUrl = PathUrl2.from("{pv1}/{pv2}/{pv3}");
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(baseUrl);

        Map<PathUrl2, PathVariableValue> expect = Map.of(new PathUrl2(new StringBuilder("path1/path2/path3/path4"), 18)
            , new PathVariableValue(Map.of("pv1", "path1",
                                           "pv2", "path2",
                                           "pv3", "path3")));

        //when
        Map<PathUrl2, PathVariableValue> actual = pathVariableSegmentChunk.internalConsume(requestUrl);
//        List<MatchedPathVariable> actuals = (List<MatchedPathVariable>) pathUrlPathVariableValueMap;

        //then
        Assertions.assertThat(actual).hasSize(1);
        Assertions.assertThat(actual).isEqualTo(expect);
    }
}