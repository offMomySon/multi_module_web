package matcher.segment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import matcher.path.PathUrl;
import matcher.path.PathVariable;
import matcher.segment.factory.SegmentChunkFactory;
import static java.util.Objects.isNull;

public class SegmentChunkChain {
    private final SegmentChunk segmentChunk;
    private SegmentChunkChain segmentChunkChain;

    public SegmentChunkChain(@NonNull SegmentChunk segmentChunk, SegmentChunkChain segmentChunkChain) {
        this.segmentChunk = segmentChunk;
        this.segmentChunkChain = segmentChunkChain;
    }

    public static SegmentChunkChain of(@NonNull PathUrl pathUrl) {
        List<SegmentChunk> segmentChunks = SegmentChunkFactory.create(pathUrl);

        SegmentChunk segmentChunk = segmentChunks.get(0);
        SegmentChunkChain headSegmentChunkChain = SegmentChunkChain.open(segmentChunk);
        SegmentChunkChain nextSegmentChunkChain = headSegmentChunkChain;
        for (int i = 1; i < segmentChunks.size(); i++) {
            segmentChunk = segmentChunks.get(i);
            nextSegmentChunkChain = nextSegmentChunkChain.chaining(segmentChunk);
        }
        nextSegmentChunkChain.close();

        return headSegmentChunkChain;
    }

    public static SegmentChunkChain open(@NonNull SegmentChunk segmentChunk) {
        return new SegmentChunkChain(segmentChunk, null);
    }

    public SegmentChunkChain close() {
        SegmentChunkChain endSegmentChunkChain = new SegmentChunkChain(new EmptySegmentChunk(), null);
        this.segmentChunkChain = endSegmentChunkChain;
        return endSegmentChunkChain;
    }

    public SegmentChunkChain chaining(@NonNull SegmentChunk segmentChunk) {
        SegmentChunkChain nextSegmentChunkChain = new SegmentChunkChain(segmentChunk, null);
        this.segmentChunkChain = nextSegmentChunkChain;
        return nextSegmentChunkChain;
    }

    public ConsumeResult consume(@NonNull PathUrl requestPathUrl) {
        List<PathUrl> remainPathUrls = segmentChunk.consume(requestPathUrl);

        boolean doesNotPossibleConsume = remainPathUrls.isEmpty();
        if (doesNotPossibleConsume) {
            return ConsumeResult.notAllConsumed();
        }

        Map<PathUrl, PathVariable> matchedPathVariables = getMatchedPathVariables(segmentChunk);

        boolean isLastChain = isNull(segmentChunkChain);
        if (isLastChain) {
            Optional<PathUrl> optionalEmtpyPathUrl = remainPathUrls.stream().filter(PathUrl::isEmpty).findFirst();

            boolean doesNotExistAllConsumed = optionalEmtpyPathUrl.isEmpty();
            if (doesNotExistAllConsumed) {
                return ConsumeResult.notAllConsumed();
            }

            PathUrl allConsumedPathUrl = optionalEmtpyPathUrl.get();
            PathVariable pathVariable = matchedPathVariables.getOrDefault(allConsumedPathUrl, PathVariable.empty());
            return ConsumeResult.allConsumed(pathVariable);
        }

        Optional<NextChainConsumeResult> optionalNextChainConsumeResult = remainPathUrls.stream()
            .map(this::nextChainConsume)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

        boolean doesNotAllConsumedPathUrl = optionalNextChainConsumeResult.isEmpty();
        if (doesNotAllConsumedPathUrl) {
            return ConsumeResult.notAllConsumed();
        }

        NextChainConsumeResult nextChainConsumeResult = optionalNextChainConsumeResult.get();
        PathVariable nextChainPathVariable = nextChainConsumeResult.getPathVariableValue();
        PathUrl nexChainProvidedPathUrl = nextChainConsumeResult.getProvidePathUrl();

        PathVariable pathVariable = matchedPathVariables.getOrDefault(nexChainProvidedPathUrl, PathVariable.empty());
        pathVariable = pathVariable.merge(nextChainPathVariable);
        return ConsumeResult.allConsumed(pathVariable);
    }

    private static Map<PathUrl, PathVariable> getMatchedPathVariables(SegmentChunk segmentChunk) {
        if (segmentChunk instanceof AbstractPathVariableSegmentChunk) {
            AbstractPathVariableSegmentChunk abstractPathVariableSegmentChunk = (AbstractPathVariableSegmentChunk) segmentChunk;
            return abstractPathVariableSegmentChunk.getMatchedPathVariables();
        }
        return Collections.emptyMap();
    }

    private Optional<NextChainConsumeResult> nextChainConsume(PathUrl remainPathUrl) {
        ConsumeResult nextConsumeResult = segmentChunkChain.consume(remainPathUrl);
        if (nextConsumeResult.doesNotAllConsumed()) {
            return Optional.empty();
        }

        PathVariable nextChainPathVariable = nextConsumeResult.getPathVariable();
        NextChainConsumeResult nextChainConsumeResult = new NextChainConsumeResult(remainPathUrl, nextChainPathVariable);
        return Optional.of(nextChainConsumeResult);
    }

    private static class NextChainConsumeResult {
        private final PathUrl providePathUrl;
        private final PathVariable pathVariable;

        public NextChainConsumeResult(PathUrl providePathUrl, PathVariable pathVariable) {
            if (isNull(providePathUrl) || isNull(pathVariable)) {
                throw new RuntimeException("Must segmentChunk not be null.");
            }

            this.providePathUrl = providePathUrl;
            this.pathVariable = pathVariable;
        }

        public PathUrl getProvidePathUrl() {
            return providePathUrl;
        }

        public PathVariable getPathVariableValue() {
            return pathVariable;
        }
    }

    public static class ConsumeResult {
        private final boolean isAllConsumed;
        private final PathVariable pathVariable;

        private ConsumeResult(boolean isAllConsumed, PathVariable pathVariable) {
            if (isNull(pathVariable)) {
                throw new RuntimeException("Must segmentChunk not be null.");
            }

            this.isAllConsumed = isAllConsumed;
            this.pathVariable = pathVariable;
        }

        public static ConsumeResult notAllConsumed() {
            return new ConsumeResult(false, PathVariable.empty());
        }

        public static ConsumeResult allConsumed(PathVariable pathVariable) {
            return new ConsumeResult(true, pathVariable);
        }

        public boolean isAllConsumed() {
            return isAllConsumed;
        }

        public boolean doesNotAllConsumed() {
            return !isAllConsumed();
        }

        public PathVariable getPathVariable() {
            return pathVariable;
        }
    }
}