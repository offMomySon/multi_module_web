package matcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import matcher.segment.SegmentChunk;
import matcher.segment.SegmentChunkChain;
import matcher.segment.factory.SegmentChunkFactory;
import static java.util.Collections.emptyList;

public class PathMatcher2<T> {
    private final Map<MatchToken, List<MatchElement<T>>> tokenElements;

    private PathMatcher2(@NonNull Map<MatchToken, List<MatchElement<T>>> tokenElements) {
        this.tokenElements = Map.copyOf(tokenElements);
    }

    public static <T> PathMatcher2<T> empty() {
        return new PathMatcher2<>(Collections.emptyMap());
    }

    public PathMatcher2<T> add(@NonNull MatchToken token, @NonNull PathUrl basePathUrl, @NonNull T matchResult) {
        List<MatchElement<T>> elements = tokenElements.getOrDefault(token, emptyList());

        SegmentChunkChain segmentChunkChain = createSegmentChunkChain(basePathUrl);
        MatchElement<T> matchElement = new MatchElement<>(segmentChunkChain, matchResult);
        List<MatchElement<T>> newElements = Stream.concat(elements.stream(), Stream.of(matchElement)).collect(Collectors.toUnmodifiableList());

        Map<MatchToken, List<MatchElement<T>>> newTokenElements = new HashMap<>(this.tokenElements);
        newTokenElements.put(token, newElements);
        return new PathMatcher2<>(Map.copyOf(newTokenElements));
    }

    public PathMatcher2<T> add2(@NonNull MatchToken token, @NonNull String basePathUrl, @NonNull T matchResult) {
        PathUrl pathUrl = PathUrl.of(basePathUrl);
        return add(token, pathUrl, matchResult);
    }

    public static <T> PathMatcher2<T> concat(@NonNull PathMatcher2<T> base, @NonNull PathMatcher2<T> other) {
        Map<MatchToken, List<MatchElement<T>>> newTokenElements = new HashMap<>(base.tokenElements);
        newTokenElements.putAll(other.tokenElements);
        return new PathMatcher2<>(Map.copyOf(newTokenElements));
    }

    private static SegmentChunkChain createSegmentChunkChain(PathUrl basePathUrl) {
        List<SegmentChunk> segmentChunks = SegmentChunkFactory.create(basePathUrl);
        SegmentChunk headChunk = segmentChunks.get(0);
        SegmentChunkChain headChunkChain = new SegmentChunkChain(headChunk, null);
        SegmentChunkChain nextChunkChain = headChunkChain;
        for (int i = 1; i < segmentChunks.size(); i++) {
            headChunk = segmentChunks.get(i);
            nextChunkChain = nextChunkChain.chaining(headChunk);
        }
        nextChunkChain.close();

        return headChunkChain;
    }

    public Optional<MatchedResult<T>> match(@NonNull MatchToken matchToken, @NonNull PathUrl pathUrl) {
        return tokenElements.getOrDefault(matchToken, emptyList())
            .stream()
            .map(element -> element.consumeBySegmentChain(pathUrl))
            .filter(MatchConsumeResult::isMatched)
            .map(MatchConsumeResult::toMatchedResult)
            .findFirst();
    }

    @Getter
    public static class MatchedResult<T> {
        private final T element;
        private final PathVariable pathVariable;

        public MatchedResult(@NonNull T element, @NonNull PathVariable pathVariable) {
            this.element = element;
            this.pathVariable = pathVariable;
        }
    }

    private static class MatchElement<T> {
        private final SegmentChunkChain segmentChunkChain;
        private final T element;

        public MatchElement(SegmentChunkChain segmentChunkChain, T element) {
            this.segmentChunkChain = segmentChunkChain;
            this.element = element;
        }

        public MatchConsumeResult<T> consumeBySegmentChain(PathUrl pathUrl) {
            SegmentChunkChain.ConsumeResult consume = segmentChunkChain.consume(pathUrl);
            return new MatchConsumeResult<>(consume, element);
        }
    }

    private static class MatchConsumeResult<T> {
        private final SegmentChunkChain.ConsumeResult consumeResult;
        private final T matchElement;

        public MatchConsumeResult(SegmentChunkChain.ConsumeResult consumeResult, T matchElement) {
            this.consumeResult = consumeResult;
            this.matchElement = matchElement;
        }

        public boolean isMatched() {
            return consumeResult.isAllConsumed();
        }

        public MatchedResult<T> toMatchedResult() {
            return new MatchedResult<>(matchElement, consumeResult.getPathVariableValue());
        }
    }

    @EqualsAndHashCode
    public static class MatchToken {
        private final String value;

        public MatchToken(@NonNull String value) {
            this.value = value;
        }
    }
}