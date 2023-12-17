package matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import matcher.segment.SegmentChunkChain;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toUnmodifiableList;
import static matcher.segment.SegmentChunkChain.ConsumeResult;

public class PathMatcher<T> {
    private final Map<Token, List<MatchConsumer<T>>> tokenConsumers;

    private PathMatcher(@NonNull Map<Token, List<MatchConsumer<T>>> tokenConsumers) {
        this.tokenConsumers = Map.copyOf(tokenConsumers);
    }

    public static <T> PathMatcher<T> empty() {
        return new PathMatcher<>(emptyMap());
    }

    public PathMatcher<T> add(@NonNull Token token, @NonNull PathUrl basePathUrl, @NonNull T element) {
        List<MatchConsumer<T>> consumers = tokenConsumers.getOrDefault(token, emptyList());

        SegmentChunkChain segmentChunkChain = SegmentChunkChain.of(basePathUrl);
        MatchConsumer<T> matchConsumer = new MatchConsumer<>(segmentChunkChain, element);
        List<MatchConsumer<T>> newConsumers = Stream.concat(consumers.stream(), Stream.of(matchConsumer)).collect(toUnmodifiableList());

        Map<Token, List<MatchConsumer<T>>> newTokenConsumers = new HashMap<>(this.tokenConsumers);
        newTokenConsumers.put(token, newConsumers);
        return new PathMatcher<>(Map.copyOf(newTokenConsumers));
    }

    public PathMatcher<T> add(@NonNull Token token, @NonNull String _basePathUrl, @NonNull T element) {
        PathUrl basePathUrl = PathUrl.of(_basePathUrl);
        return add(token, basePathUrl, element);
    }

    public static <T> PathMatcher<T> concat(@NonNull PathMatcher<T> base, @NonNull PathMatcher<T> other) {
        Map<Token, List<MatchConsumer<T>>> newTokenConsumers = new HashMap<>(base.tokenConsumers);
        newTokenConsumers.putAll(other.tokenConsumers);
        return new PathMatcher<>(Map.copyOf(newTokenConsumers));
    }

    public Optional<MatchedElement<T>> match(@NonNull Token token, @NonNull PathUrl pathUrl) {
        return tokenConsumers.getOrDefault(token, emptyList())
            .stream()
            .map(matchConsumer -> matchConsumer.consume(pathUrl))
            .filter(MatchConsumeResult::isMatched)
            .map(MatchConsumeResult::toMatchedElement)
            .findFirst();
    }

    @EqualsAndHashCode
    public static class Token {
        private final String value;

        public Token(@NonNull String value) {
            this.value = value;
        }
    }

    private static class MatchConsumer<T> {
        private final SegmentChunkChain segmentChunkChain;
        private final T element;

        public MatchConsumer(@NonNull SegmentChunkChain segmentChunkChain, @NonNull T element) {
            this.segmentChunkChain = segmentChunkChain;
            this.element = element;
        }

        public MatchConsumeResult<T> consume(@NonNull PathUrl pathUrl) {
            ConsumeResult result = segmentChunkChain.consume(pathUrl);
            return new MatchConsumeResult<T>(this.element, result);
        }
    }

    private static class MatchConsumeResult<T> {
        private final T element;
        private final ConsumeResult consumeResult;

        public MatchConsumeResult(@NonNull T element, @NonNull ConsumeResult consumeResult) {
            this.element = element;
            this.consumeResult = consumeResult;
        }

        public boolean isMatched() {
            return consumeResult.isAllConsumed();
        }

        public MatchedElement<T> toMatchedElement() {
            PathVariable pathVariable = consumeResult.getPathVariable();
            return new MatchedElement<>(this.element, pathVariable);
        }
    }

    @Getter
    public static class MatchedElement<T> {
        private final T element;
        private final PathVariable pathVariable;

        public MatchedElement(@NonNull T element, @NonNull PathVariable pathVariable) {
            this.element = element;
            this.pathVariable = pathVariable;
        }
    }
}
