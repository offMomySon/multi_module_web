package mapper.newsegment.chunk;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;
import mapper.newsegment.chunk.SegmentChunk.MatchResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegacyWildCardSegmentChunkTestV2 {
    @DisplayName("null 입력시 exception 이 발생합니다.")
    @Test
    void test0() throws Exception {
        //given
        RegacyWildCardSegmentChunk regacyWildCardSegmentChunk = new RegacyWildCardSegmentChunk(List.of("**", "this"));

        //when
        Throwable actual = Assertions.catchThrowable(() -> regacyWildCardSegmentChunk.match(null));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("WildCard segment 는 어떤 segment 든 매칭될 수 있습니다.")
    @Test
    void test() throws Exception {
        //given
        String pathVariableSegment = "**";
        String provideSegment = "p1/p2/p3/p4";
        RegacyWildCardSegmentChunk segmentChunk = new RegacyWildCardSegmentChunk(List.of(pathVariableSegment));
        SegmentProvider provider = SegmentProvider.from(provideSegment);

        Set<MatchSegment> expects = Set.of(
            new MatchSegment(Map.of(pathVariableSegment, "")),
            new MatchSegment(Map.of(pathVariableSegment, "p1")),
            new MatchSegment(Map.of(pathVariableSegment, "p1/p2")),
            new MatchSegment(Map.of(pathVariableSegment, "p1/p2/p3")),
            new MatchSegment(Map.of(pathVariableSegment, "p1/p2/p3/p4"))
        );

        //when
        List<MatchResult> actual = segmentChunk.match(provider);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(expects.size());
        Set<MatchSegment> actuals = actual.stream().map(MatchResult::getMatchSegment).collect(Collectors.toUnmodifiableSet());

        Assertions.assertThat(actuals).isEqualTo(expects);
    }

    @DisplayName("WildCard segment 는 어떤 segment 든 매칭될 수 있습니다.")
    @Test
    void test6() throws Exception {
        //given
        List<String> segments = List.of("**", "{pv2}", "p6");
        String provideSegment = "p1/p2/p6/p4/p5/p6";
        RegacyWildCardSegmentChunk segmentChunk = new RegacyWildCardSegmentChunk(segments);
        SegmentProvider provider = SegmentProvider.from(provideSegment);

        Set<MatchSegment> expects = Set.of(
            new MatchSegment(Map.of("**", "p1/p2/p6/p4", "{pv2}", "p5", "p6", "p6")),
            new MatchSegment(Map.of("**", "p1", "{pv2}", "p2", "p6", "p6"))
        );

        //when
        List<MatchResult> actual = segmentChunk.match(provider);
        for (MatchResult matchResult : actual) {
            System.out.println(matchResult);
        }

        //then
        Assertions.assertThat(actual.size()).isEqualTo(expects.size());
        Set<MatchSegment> actuals = actual.stream().map(MatchResult::getMatchSegment).collect(Collectors.toUnmodifiableSet());

        Assertions.assertThat(actuals).isEqualTo(expects);
    }
}