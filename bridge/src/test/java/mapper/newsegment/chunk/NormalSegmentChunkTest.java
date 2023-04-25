package mapper.newsegment.chunk;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NormalSegmentChunkTest {
    @DisplayName("null 입력시 exception 이 발생합니다.")
    @Test
    void test0() throws Exception {
        //given
        NormalSegmentChunk normalSegmentChunk = NormalSegmentChunk.from("this");

        //when
        Throwable actual = Assertions.catchThrowable(() -> normalSegmentChunk.match(null));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("모든 segment 가 일치하면 결과값을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideChunkAndSegmentProviderAndExpectMatchSegment")
    void test1(String[] chunk, SegmentProvider provider, MatchSegment expectMatchSegment) throws Exception {
        //given
        NormalSegmentChunk normalSegmentChunk = NormalSegmentChunk.from(chunk);

        //when
        List<SegmentChunk.MatchResult> actual = normalSegmentChunk.match(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(1);
        SegmentChunk.MatchResult matchResult = actual.get(0);
        MatchSegment actualMatchSegment = matchResult.getMatchSegment();

        Assertions.assertThat(actualMatchSegment).isEqualTo(expectMatchSegment);
    }

    @DisplayName("실패시 빈 리스트를 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideChunkAndSegmentProviderAndExpectMatchSegment")
    void test3() throws Exception {
        //given
        NormalSegmentChunk normalSegmentChunk = NormalSegmentChunk.from("pv1", "pv2");
        SegmentProvider provider = SegmentProvider.from(List.of("pv1"));

        //when
        List<SegmentChunk.MatchResult> actual = normalSegmentChunk.match(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(0);
    }


    @DisplayName("다음 매칭할 segment 를 반환합니다.")
    @Test
    void test2() throws Exception {
        //given
        Queue<String> queue = new ArrayDeque<>();
        queue.add("");
        queue.add("path");
        SegmentProvider provider = new SegmentProvider(queue);
        EmptySegmentChunk emptySegmentChunk = new EmptySegmentChunk();

        Queue<String> expectQueue = new ArrayDeque<>();
        expectQueue.add("path");
        SegmentProvider expectProvider = new SegmentProvider(expectQueue);

        //when
        List<SegmentChunk.MatchResult> actual = emptySegmentChunk.match(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(1);

        SegmentChunk.MatchResult actualMatchResult = actual.get(0);
        SegmentProvider actualProvider = actualMatchResult.getLeftSegments();

        Assertions.assertThat(actualProvider).isEqualTo(expectProvider);
    }

    @DisplayName("매칭할 segment 가 제공되는 segment 보다 많으면 빈 결과를 반환합니다.")
    @Test
    void test4() throws Exception {
        //given
        SegmentProvider provider = SegmentProvider.from("/pv1/pv2");
        NormalSegmentChunk normalSegmentChunk = NormalSegmentChunk.from("pv1", "{pv2}", "{pv3}", "pv4");

        //when
        List<SegmentChunk.MatchResult> actual = normalSegmentChunk.match(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(0);
    }


    public static Stream<Arguments> provideChunkAndSegmentProviderAndExpectMatchSegment() {
        String[] chunk = new String[]{"pv1", "pv2", "pv3"};
        SegmentProvider successProvider = SegmentProvider.from(List.of("pv1", "pv2", "pv3"));
        MatchSegment matchSegment = new MatchSegment(Map.of("pv1", "pv1", "pv2", "pv2", "pv3", "pv3"));

        return Stream.of(
            Arguments.of(chunk, successProvider, matchSegment)
        );
    }
}