package mapper.newsegment.chunk;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;
import mapper.newsegment.chunk.SegmentChunk.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PathVariableSegmentChunkTest {
    @DisplayName("null 입력시 exception 이 발생합니다.")
    @Test
    void test0() throws Exception {
        //given
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk("this");

        //when
        Throwable actual = Assertions.catchThrowable(() -> pathVariableSegmentChunk.consume(null));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("PathVariable segment 는 어떤 segment 든 매칭될 수 있습니다.")
    @Test
    void test() throws Exception {
        //given
        String pathVariableSegment = "{pv1}";
        String provideSegment = "value1";
        PathVariableSegmentChunk segmentChunk = new PathVariableSegmentChunk(pathVariableSegment);
        SegmentProvider provider = SegmentProvider.from(provideSegment);

        MatchSegment expectMatchSegment = new MatchSegment(Map.of(pathVariableSegment, provideSegment));

        //when
        List<Result> actual = segmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(1);
        Result result = actual.get(0);
        MatchSegment actualMatchSegment = result.getMatchSegment();
        SegmentProvider actualProvider = result.getLeftSegments();

        Assertions.assertThat(actualMatchSegment).isEqualTo(expectMatchSegment);
        Assertions.assertThat(actualProvider).isEqualTo(SegmentProvider.empty());
    }


    @DisplayName("모든 segment 가 일치하면 결과값을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideChunkAndSegmentProviderAndExpectMatchSegment")
    void test1(String[] chunk, SegmentProvider provider, MatchSegment expectMatchSegment) throws Exception {
        //given
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(chunk);

        //when
        List<Result> actual = pathVariableSegmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(1);
        Result result = actual.get(0);
        MatchSegment actualMatchSegment = result.getMatchSegment();

        Assertions.assertThat(actualMatchSegment).isEqualTo(expectMatchSegment);
    }

    @DisplayName("실패시 빈 리스트를 반환합니다.")
    @Test
    void test3() throws Exception {
        //given
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk("pv1", "pv2");
        SegmentProvider provider = SegmentProvider.from(List.of("pv1"));

        //when
        List<Result> actual = pathVariableSegmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(0);
    }


    @DisplayName("매칭에 참여하지 않은 segment 를 반환합니다.")
    @Test
    void test2() throws Exception {
        //given
        SegmentProvider provider = SegmentProvider.from("/pv1/pv2/pv3/pv4/pv5");
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk("pv1", "{pv2}", "{pv3}", "pv4");

        SegmentProvider expectProvider = SegmentProvider.from("pv5");

        //when
        List<Result> actual = pathVariableSegmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(1);

        Result actualResult = actual.get(0);
        SegmentProvider actualProvider = actualResult.getLeftSegments();

        Assertions.assertThat(actualProvider).isEqualTo(expectProvider);
    }

    public static Stream<Arguments> provideChunkAndSegmentProviderAndExpectMatchSegment() {
        String[] chunk = new String[]{"pv1", "{pv2}", "pv3", "{pv4}"};
        SegmentProvider successProvider = SegmentProvider.from(List.of("pv1", "pv2", "pv3", "pv4"));
        MatchSegment matchSegment = new MatchSegment(Map.of("pv1", "pv1", "{pv2}", "pv2", "pv3", "pv3", "{pv4}", "pv4"));

        return Stream.of(
            Arguments.of(chunk, successProvider, matchSegment)
        );
    }
}