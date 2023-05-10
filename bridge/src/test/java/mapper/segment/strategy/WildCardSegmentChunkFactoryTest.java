package mapper.segment.strategy;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segment.PathUrl;
import mapper.segment.SegmentChunk;
import mapper.segment.WildCardPathVariableSegmentChunk;
import mapper.segment.WildCardSegmentChunk;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WildCardSegmentChunkFactoryTest {
    @DisplayName("wildcard 가 첫번째 segment 가 아니면 exception 이 발생합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl pathUrl = PathUrl.from("/doesNotWildCard/**/path1");

        //when
        Throwable actual = Assertions.catchThrowable(() -> WildCardSegmentChunkCreateStrategy.create(pathUrl));

        //then
        Assertions.assertThat(actual).isNotNull();
    }


    @DisplayName("PathUrl 에 따른 segmentchunks 를 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideSegmentChunks")
    void ttest(String baseUrl, List<? extends Class<? extends SegmentChunk>> expectInstances) throws Exception {
        //given
        PathUrl pathUrl = PathUrl.from(baseUrl);

        //when
        Deque<SegmentChunk> actuals = WildCardSegmentChunkCreateStrategy.create(pathUrl);
        List<? extends Class<? extends SegmentChunk>> actualInstances = actuals.stream().map(SegmentChunk::getClass).collect(Collectors.toUnmodifiableList());

        //then
        // todo 어떻게 테스트하지 아래 cotainSequences 로 테스트하고 싶은데
        // Assertions.assertThat(actualInstances).containsSequences(expects);
        for (int i = 0; i < expectInstances.size(); i++) {
            Class<? extends SegmentChunk> actual = actualInstances.get(i);
            Class<? extends SegmentChunk> expect = expectInstances.get(i);

            Assertions.assertThat(actual).isEqualTo(expect);
        }
    }

    public static Stream<Arguments> provideSegmentChunks() {
        return Stream.of(
            Arguments.of("/**", List.of(WildCardSegmentChunk.class)),
            Arguments.of("/**/pv", List.of(WildCardSegmentChunk.class)),
            Arguments.of("/**/pv/pv2", List.of(WildCardSegmentChunk.class)),
            Arguments.of("/**/{pv}", List.of(WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/**/pv/{pv}", List.of(WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/**/{pv}/pv", List.of(WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/**/path2/**/path3", List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/**/path2/**/path3/**/path4", List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/**/{pv1}/path2/**/path3", List.of(WildCardPathVariableSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/**/{pv1}/path2/**/path3/**/path4", List.of(WildCardPathVariableSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/**/path2/**/{pv2}/path3", List.of(WildCardSegmentChunk.class, WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/**/path2/**/{pv2}/path3/**/{pv2}/path3", List.of(WildCardSegmentChunk.class, WildCardPathVariableSegmentChunk.class, WildCardPathVariableSegmentChunk.class))
        );
    }
}