package main.matcher.segment.strategy;

import matcher.segment.EmptySegmentChunk;
import matcher.segment.factory.GeneralSegmentChunkCreateStrategy;
import matcher.segment.NormalSegmentChunk;
import matcher.segment.PathUrl2;
import matcher.segment.PathVariableSegmentChunk;
import matcher.segment.SegmentChunk;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeneralSegmentChunkFactoryTest {

    @DisplayName("basepath url 이 비어있으면 빈 결과를 반환합니다.")
    @Test
    void tttest() throws Exception {
        //given
        PathUrl2 emptyPathUrl = PathUrl2.empty();
        GeneralSegmentChunkCreateStrategy generalSegmentChunkCreateStrategy = new GeneralSegmentChunkCreateStrategy();

        //when
        List<SegmentChunk> actuals = generalSegmentChunkCreateStrategy.create(emptyPathUrl);

        //then
        Assertions.assertThat(actuals).hasSize(1);
        SegmentChunk actual = actuals.get(0);
        Assertions.assertThat(actual).isInstanceOf(EmptySegmentChunk.class);
    }

    @DisplayName("PathUrl 에 pathVariable 이 존재하면 pathVariableSegmentChunk 를 생성합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl2 pathUrl = PathUrl2.from("/path1/{pv1}/path2");
        GeneralSegmentChunkCreateStrategy generalSegmentChunkCreateStrategy = new GeneralSegmentChunkCreateStrategy();

        //when
        List<SegmentChunk> actuals = generalSegmentChunkCreateStrategy.create(pathUrl);

        //then
        Assertions.assertThat(actuals).hasSize(1);
        SegmentChunk actual = actuals.get(0);
        Assertions.assertThat(actual).isInstanceOf(PathVariableSegmentChunk.class);
    }

    @DisplayName("PathUrl 에 pathVariable 이 존재하지 않으면 normalSegmentChunk 를 생성합니다.")
    @Test
    void ttest() throws Exception {
        //given
        PathUrl2 pathUrl = PathUrl2.from("/path1/path2/path3");
        GeneralSegmentChunkCreateStrategy generalSegmentChunkCreateStrategy = new GeneralSegmentChunkCreateStrategy();

        //when
        List<SegmentChunk> actuals = generalSegmentChunkCreateStrategy.create(pathUrl);

        //then
        Assertions.assertThat(actuals).hasSize(1);
        SegmentChunk actual = actuals.get(0);
        Assertions.assertThat(actual).isInstanceOf(NormalSegmentChunk.class);
    }
}