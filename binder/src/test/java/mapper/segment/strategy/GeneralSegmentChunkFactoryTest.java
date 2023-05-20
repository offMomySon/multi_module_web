package mapper.segment.strategy;

import java.util.List;
import mapper.segment.EmptySegmentChunk;
import mapper.segment.GeneralSegmentChunkCreateCreateStrategy;
import mapper.segment.NormalSegmentChunk;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableSegmentChunk;
import mapper.segment.SegmentChunk;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeneralSegmentChunkFactoryTest {

    @DisplayName("basepath url 이 비어있으면 빈 결과를 반환합니다.")
    @Test
    void tttest() throws Exception {
        //given
        PathUrl emptyPathUrl = PathUrl.empty();
        GeneralSegmentChunkCreateCreateStrategy generalSegmentChunkCreateStrategy = new GeneralSegmentChunkCreateCreateStrategy();

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
        PathUrl pathUrl = PathUrl.from("/path1/{pv1}/path2");
        GeneralSegmentChunkCreateCreateStrategy generalSegmentChunkCreateStrategy = new GeneralSegmentChunkCreateCreateStrategy();

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
        PathUrl pathUrl = PathUrl.from("/path1/path2/path3");
        GeneralSegmentChunkCreateCreateStrategy generalSegmentChunkCreateStrategy = new GeneralSegmentChunkCreateCreateStrategy();

        //when
        List<SegmentChunk> actuals = generalSegmentChunkCreateStrategy.create(pathUrl);

        //then
        Assertions.assertThat(actuals).hasSize(1);
        SegmentChunk actual = actuals.get(0);
        Assertions.assertThat(actual).isInstanceOf(NormalSegmentChunk.class);
    }
}