package mapper.segmentv3.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segmentv3.NormalSegmentChunk;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariableSegmentChunk;
import mapper.segmentv3.SegmentChunk;
import mapper.segmentv3.WildCardPathVariableSegmentChunk;
import mapper.segmentv3.WildCardSegmentChunk;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SegmentChunkCreateFactoryTest {

    @DisplayName("PathUrl 에 따른 생성된 segmentchunks 를 반환합니다.")
    @ParameterizedTest
    @MethodSource("provideSegmentChunks")
    void ttest(String baseUrl, List<? extends Class<? extends SegmentChunk>> expectInstances) throws Exception {
        //given
        PathUrl pathUrl = PathUrl.from(baseUrl);

        //when
        List<SegmentChunk> actuals = SegmentChunkCreateFactory.create(pathUrl);
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