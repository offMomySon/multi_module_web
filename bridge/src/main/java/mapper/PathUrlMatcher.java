package mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableValue;
import mapper.segment.SegmentChunk;
import mapper.segment.SegmentChunkChain;
import mapper.segment.strategy.SegmentChunkFactory;

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

        List<SegmentChunk> segmentChunks = segmentChunkFactory.create();
        Collections.reverse(segmentChunks);
        SegmentChunkChain baseSegmentChunkChain = segmentChunks.stream()
            .reduce(SegmentChunkChain.empty(), SegmentChunkChain::link, SegmentChunkChain::link);

        List<PathUrl> leftPathUrl = baseSegmentChunkChain.consume(requestUrl);
        boolean doesNotMatch = leftPathUrl.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = baseSegmentChunkChain.getPathVariable();
        return Optional.of(pathVariableValue);
    }
}
