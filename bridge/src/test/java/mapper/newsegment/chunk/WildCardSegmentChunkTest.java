package mapper.newsegment.chunk;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

class WildCardSegmentChunkTest {

    @DisplayName("")
    @Test
    void test123() throws Exception {
        //given

        List<String> pv = List.of("pv");

        List<String> strings = pv.subList(0, 0);
        System.out.println(strings.size());
        System.out.println(strings);

        //when

        //then

    }

    @DisplayName("null 입력시 exception 이 발생합니다.")
    @Test
    void test0() throws Exception {
        //given
        WildCardSegmentChunk wildCardSegmentChunk = new WildCardSegmentChunk(List.of("this"));

        //when
        Throwable actual = Assertions.catchThrowable(() -> wildCardSegmentChunk.consume(null));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("WildCard segment 는 어떤 segment 든 매칭될 수 있습니다.")
    @Test
    void test() throws Exception {
        //given
        String pathVariableSegment = "**";
        String provideSegment = "p1/p2/p3/p4";
        WildCardSegmentChunk segmentChunk = new WildCardSegmentChunk(List.of(pathVariableSegment));
        SegmentProvider provider = SegmentProvider.from(provideSegment);

        Set<MatchSegment> expects = Set.of(
            new MatchSegment(Map.of(pathVariableSegment, "")),
            new MatchSegment(Map.of(pathVariableSegment, "p1")),
            new MatchSegment(Map.of(pathVariableSegment, "p1/p2")),
            new MatchSegment(Map.of(pathVariableSegment, "p1/p2/p3")),
            new MatchSegment(Map.of(pathVariableSegment, "p1/p2/p3/p4"))
        );

        //when
        List<Result> actual = segmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(expects.size());
        Set<MatchSegment> actuals = actual.stream().map(Result::getMatchSegment).collect(Collectors.toUnmodifiableSet());

        Assertions.assertThat(actuals).isEqualTo(expects);
    }

    @DisplayName("WildCard segment 는 어떤 segment 든 매칭될 수 있습니다.")
    @Test
    void test6() throws Exception {
        //given
        List<String> segments = List.of("**", "{pv2}", "p6");
        String provideSegment = "p1/p2/p6/p4/p5/p6";
        WildCardSegmentChunk segmentChunk = new WildCardSegmentChunk(segments);
        SegmentProvider provider = SegmentProvider.from(provideSegment);

        Set<MatchSegment> expects = Set.of(
            new MatchSegment(Map.of("**", "p1/p2/p6/p4", "{pv2}", "p5", "p6", "p6")),
            new MatchSegment(Map.of("**", "p1", "{pv2}", "p2", "p6", "p6"))
        );

        //when
        List<Result> actual = segmentChunk.consume(provider);
        for (Result result : actual) {
            System.out.println(result);
        }

        //then
        Assertions.assertThat(actual.size()).isEqualTo(expects.size());
        Set<MatchSegment> actuals = actual.stream().map(Result::getMatchSegment).collect(Collectors.toUnmodifiableSet());

        Assertions.assertThat(actuals).isEqualTo(expects);
    }


    @DisplayName("모든 segment 가 일치하면 결과값을 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideChunkAndSegmentProviderAndExpectMatchSegment")
    void test1(String[] chunk, SegmentProvider provider, MatchSegment expectMatchSegment) throws Exception {
        //given
        WildCardSegmentChunk wildCardSegmentChunk = WildCardSegmentChunk.from(chunk);

        //when
        List<Result> actual = wildCardSegmentChunk.consume(provider);

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
        WildCardSegmentChunk wildCardSegmentChunk = WildCardSegmentChunk.from("pv1", "pv2");
        SegmentProvider provider = SegmentProvider.from(List.of("pv1"));

        //when
        List<Result> actual = wildCardSegmentChunk.consume(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(0);
    }


    @DisplayName("매칭에 참여하지 않은 segment 를 반환합니다.")
    @Test
    void test2() throws Exception {
        //given
        SegmentProvider provider = SegmentProvider.from("/pv1/pv2/pv3/pv4/pv5");
        WildCardSegmentChunk wildCardSegmentChunk = WildCardSegmentChunk.from("pv1", "{pv2}", "{pv3}", "pv4");

        SegmentProvider expectProvider = SegmentProvider.from("pv5");

        //when
        List<Result> actual = wildCardSegmentChunk.consume(provider);

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