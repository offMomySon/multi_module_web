package mapper.segmentv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WildCardPathVariableSegmentChunk implements SegmentChunk {
    private static final String WILD_CARD = "**";

    private final PathUrl baseUrl;
    private final List<MatchPathVariable> matchPathVariables = new ArrayList<>();

    public WildCardPathVariableSegmentChunk(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);

        String segment = baseUrl.peekSegment();
        boolean doesNotWildCardAtFirst = !WILD_CARD.equals(segment);
        if (doesNotWildCardAtFirst) {
            throw new RuntimeException("first segment does not wild card.");
        }

        List<String> baseSegments = baseUrl.toList();
        boolean doesNotExistPathVariableSegments = baseSegments.stream().noneMatch(PathVariableUtil::isPathVariable);
        if (doesNotExistPathVariableSegments) {
            throw new RuntimeException("does not exist PathVariableSegment");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl> consume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl copiedBaseUrl = baseUrl.copy();
        copiedBaseUrl.popSegment();
        PathVariableSegmentChunk pathVariableSegmentChunk = new PathVariableSegmentChunk(copiedBaseUrl);

        PathUrl copiedRequestUrl = requestUrl.copy();

        List<PathUrl> resultPathUrls = new ArrayList<>();
        while (copiedRequestUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestUrl = copiedBaseUrl.size() > copiedRequestUrl.size();
            if (doesNotSufficientRequestUrl) {
                break;
            }

            List<PathUrl> leftPathUrls = pathVariableSegmentChunk.consume(copiedRequestUrl);

            boolean doesNotMatch = leftPathUrls.isEmpty();
            if (doesNotMatch) {
                copiedRequestUrl.popSegment();
                continue;
            }

            PathUrl leftPathUrl = leftPathUrls.get(0);
            resultPathUrls.add(leftPathUrl);

            PathVariable matchPathVariable = pathVariableSegmentChunk.getPathVariable();
            MatchPathVariable pathUrlPathVariable = new MatchPathVariable(leftPathUrl, matchPathVariable);

            matchPathVariables.add(pathUrlPathVariable);

            copiedRequestUrl.popSegment();
        }


        return resultPathUrls;
    }

    public List<MatchPathVariable> getMatchPathVaraible() {
        return matchPathVariables;
    }

    public static class MatchPathVariable {
        private final PathUrl leftPathUrl;
        private final PathVariable pathVariable;

        public MatchPathVariable(PathUrl leftPathUrl, PathVariable pathVariable) {
            Objects.requireNonNull(leftPathUrl);
            Objects.requireNonNull(pathVariable);

            this.leftPathUrl = leftPathUrl;
            this.pathVariable = pathVariable;
        }

        public PathUrl getLeftPathUrl() {
            return leftPathUrl;
        }

        public PathVariable getPathVariable() {
            return pathVariable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchPathVariable that = (MatchPathVariable) o;
            return Objects.equals(leftPathUrl, that.leftPathUrl) && Objects.equals(pathVariable, that.pathVariable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(leftPathUrl, pathVariable);
        }

        @Override
        public String toString() {
            return "MatchPathVariable{" +
                "leftPathUrl=" + leftPathUrl +
                ", pathVariable=" + pathVariable +
                '}';
        }
    }
}
