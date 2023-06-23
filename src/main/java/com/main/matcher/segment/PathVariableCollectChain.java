package com.main.matcher.segment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PathVariableCollectChain {
    private final PathVariableCollectChain segmentChunkChain;
    private final SegmentChunk segmentChunk;

    public PathVariableCollectChain(PathVariableCollectChain segmentChunkChain, SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        this.segmentChunkChain = segmentChunkChain;
        this.segmentChunk = segmentChunk;
    }

    public static PathVariableCollectChain empty() {
        return new PathVariableCollectChain(null, new EmptySegmentChunk());
    }

    public PathVariableCollectChain chaining(SegmentChunk segmentChunk) {
        Objects.requireNonNull(segmentChunk);
        return new PathVariableCollectChain(this, segmentChunk);
    }

    public ConsumeResult consume(PathUrl requestPathUrl) {
        Objects.requireNonNull(requestPathUrl);

        List<PathUrl> remainPathUrls = segmentChunk.consume(requestPathUrl);

        boolean doesNotPossibleConsume = remainPathUrls.isEmpty();
        if (doesNotPossibleConsume) {
            return ConsumeResult.notAllConsumed();
        }

        Map<PathUrl, PathVariableValue> matchedPathVariables = getMatchedPathVariables(segmentChunk);

        boolean doesNotExistNextChain = Objects.isNull(segmentChunkChain);
        if (doesNotExistNextChain) {
            Optional<PathUrl> optionalEmtpyPathUrl = remainPathUrls.stream().filter(PathUrl::isEmtpy).findFirst();

            boolean doesNotAllConsumedPathUrl = optionalEmtpyPathUrl.isEmpty();
            if (doesNotAllConsumedPathUrl) {
                return ConsumeResult.notAllConsumed();
            }

            PathUrl allConsumedPathUrl = optionalEmtpyPathUrl.get();
            PathVariableValue pathVariableValue = matchedPathVariables.getOrDefault(allConsumedPathUrl, PathVariableValue.empty());
            return ConsumeResult.allConsumed(pathVariableValue);
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
        PathVariableValue nextChainPathVariableValue = nextChainConsumeResult.getPathVariableValue();
        PathUrl nexChainProvidedPathUrl = nextChainConsumeResult.getProvidePathUrl();

        PathVariableValue pathVariableValue = matchedPathVariables.getOrDefault(nexChainProvidedPathUrl, PathVariableValue.empty());
        pathVariableValue = pathVariableValue.merge(nextChainPathVariableValue);
        return ConsumeResult.allConsumed(pathVariableValue);
    }

    private static Map<PathUrl, PathVariableValue> getMatchedPathVariables(SegmentChunk segmentChunk) {
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

        PathVariableValue nextChainPathVariableValue = nextConsumeResult.getPathVariableValue();
        NextChainConsumeResult nextChainConsumeResult = new NextChainConsumeResult(remainPathUrl, nextChainPathVariableValue);
        return Optional.of(nextChainConsumeResult);
    }

    private static class NextChainConsumeResult {
        private final PathUrl providePathUrl;
        private final PathVariableValue pathVariableValue;

        public NextChainConsumeResult(PathUrl providePathUrl, PathVariableValue pathVariableValue) {
            this.providePathUrl = providePathUrl;
            this.pathVariableValue = pathVariableValue;
        }

        public PathUrl getProvidePathUrl() {
            return providePathUrl;
        }

        public PathVariableValue getPathVariableValue() {
            return pathVariableValue;
        }
    }

    public static class ConsumeResult {
        private final boolean isAllConsumed;
        private final PathVariableValue pathVariableValue;

        private ConsumeResult(boolean isAllConsumed, PathVariableValue pathVariableValue) {
            Objects.requireNonNull(pathVariableValue);
            this.isAllConsumed = isAllConsumed;
            this.pathVariableValue = pathVariableValue;
        }

        public static ConsumeResult notAllConsumed() {
            return new ConsumeResult(false, PathVariableValue.empty());
        }

        public static ConsumeResult allConsumed(PathVariableValue pathVariableValue) {
            return new ConsumeResult(true, pathVariableValue);
        }

        public boolean isAllConsumed() {
            return isAllConsumed;
        }

        public boolean doesNotAllConsumed() {
            return !isAllConsumed();
        }

        public PathVariableValue getPathVariableValue() {
            return pathVariableValue;
        }
    }
}
