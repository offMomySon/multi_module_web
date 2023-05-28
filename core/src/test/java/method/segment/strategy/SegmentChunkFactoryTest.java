package method.segment.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import method.segment.EmptySegmentChunk;
import method.segment.NormalSegmentChunk;
import method.segment.PathUrl;
import method.segment.PathVariableSegmentChunk;
import method.segment.SegmentChunk;
import method.segment.SegmentChunkFactory;
import method.segment.WildCardPathVariableSegmentChunk;
import method.segment.WildCardSegmentChunk;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SegmentChunkFactoryTest {

    @DisplayName("PathUrl 에 따른 생성된 segmentchunks 를 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideSegmentChunks")
    void ttest(String baseUrl, List<? extends Class<? extends SegmentChunk>> expectInstances) throws Exception {
        //given
        PathUrl pathUrl = PathUrl.from(baseUrl);

        //when
        List<SegmentChunk> actuals = new SegmentChunkFactory(pathUrl).create();
        List<? extends Class<? extends SegmentChunk>> actualInstances = actuals.stream().map(SegmentChunk::getClass).collect(Collectors.toUnmodifiableList());

        //then
        Assertions.assertThat(actualInstances).isEqualTo(expectInstances);
    }

    public static Stream<Arguments> provideSegmentChunks() {
        return Stream.of(
            Arguments.of("/", List.of(EmptySegmentChunk.class)),
            Arguments.of("/**", List.of(WildCardSegmentChunk.class)),
            Arguments.of("/**/{pv}", List.of(WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/path1", List.of(NormalSegmentChunk.class)),
            Arguments.of("/path1/{pv}", List.of(PathVariableSegmentChunk.class)),
            Arguments.of("/path1/**", List.of(NormalSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/path1/**/{pv}", List.of(NormalSegmentChunk.class, WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/path1/**/pv/{pv}", List.of(NormalSegmentChunk.class, WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/path1/**/{pv}/pv", List.of(NormalSegmentChunk.class, WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/path1/**/pv", List.of(NormalSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/path1/**/pv/pv2", List.of(NormalSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/path1/{pv1}/**/path2/**/path3", List.of(PathVariableSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/path1/{pv1}/**/path2/**/path3/**/path4", List.of(PathVariableSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/path1/{pv1}/**/{pv1}/path2/**/path3", List.of(PathVariableSegmentChunk.class, WildCardPathVariableSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/path1/{pv1}/**/{pv1}/path2/**/path3/**/path4",
                         List.of(PathVariableSegmentChunk.class, WildCardPathVariableSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class)),
            Arguments.of("/path1/{pv1}/**/path2/**/{pv2}/path3", List.of(PathVariableSegmentChunk.class, WildCardSegmentChunk.class, WildCardPathVariableSegmentChunk.class)),
            Arguments.of("/path1/{pv1}/**/path2/**/{pv2}/path3/**/{pv2}/path3",
                         List.of(PathVariableSegmentChunk.class, WildCardSegmentChunk.class, WildCardPathVariableSegmentChunk.class, WildCardPathVariableSegmentChunk.class))
        );
    }
}