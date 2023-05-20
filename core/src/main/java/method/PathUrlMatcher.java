package method;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import method.segment.PathUrl;
import method.segment.PathVariableCollectChainV2;
import method.segment.PathVariableValue;
import method.segment.SegmentChunk;
import method.segment.SegmentChunkFactory;

// TODO
// SegmentChunkChain 을 생성자로 선택
// 장.
// 단.
//      테스트 어렵다.
//      SegmentChunkChain 의 의존성이 PathUrlMatcher 뿐 아니라 PathUrlMatcher 을 사용하는 모든곳에 퍼진다.
//
// SegmentChunkFactory 을 생성자로 선택
// 장.
//      생성이 쉽다.
// 단.
//      호출 될 때 마다 SegmentChunkChain 을 생성해야한다.

public class PathUrlMatcher {
    private final SegmentChunkFactory segmentChunkFactory;

    private PathUrlMatcher(SegmentChunkFactory segmentChunkFactory) {
        Objects.requireNonNull(segmentChunkFactory);
        this.segmentChunkFactory = segmentChunkFactory;
    }

    public static PathUrlMatcher from(PathUrl baseUrl) {
        if (Objects.isNull(baseUrl)) {
            throw new RuntimeException("_baseUrl is empty.");
        }
        SegmentChunkFactory segmentChunkFactory = new SegmentChunkFactory(baseUrl);
        return new PathUrlMatcher(segmentChunkFactory);
    }

    public Optional<PathVariableValue> match(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        // TODO
        // combiner 처리를 고민해봤지만 떠오르지 않는다.
        // merge 느낌이아니라 link 느낌이기 때문에 안되는것 같다.
//        List<SegmentChunk> segmentChunks = segmentChunkFactory.create();
//        Collections.reverse(segmentChunks);
//        PathVariableCollectChainV2 baseSegmentChunkChain = segmentChunks.stream().reduce(PathVariableCollectChainV2.empty(),
//                                                                                         PathVariableCollectChainV2::chaining,
//                                                                                         (sc1, sc2) -> null);
        List<SegmentChunk> segmentChunks = segmentChunkFactory.create();
        Collections.reverse(segmentChunks);
        PathVariableCollectChainV2 baseSegmentChunkChain = PathVariableCollectChainV2.empty();
        for (SegmentChunk segmentChunk : segmentChunks) {
            baseSegmentChunkChain = baseSegmentChunkChain.chaining(segmentChunk);
        }

        Optional<PathVariableValue> optionalMatchPathVariableValue = baseSegmentChunkChain.consume(requestUrl);
        boolean doesNotMatch = optionalMatchPathVariableValue.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = optionalMatchPathVariableValue.get();
        return Optional.of(pathVariableValue);
    }
}
