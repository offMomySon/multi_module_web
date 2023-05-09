package mapper.segmentv3;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import mapper.segmentv3.pathvariable.AbstractPathVariableSegmentChunk;
import mapper.segmentv3.strategy.SegmentChunkFactory;

public class SegmentManager {
    public static Optional<PathVariableValue> consume(String _baseUrl, String _requestUrl) {
        if (Objects.isNull(_requestUrl) || _requestUrl.isBlank()) {
            throw new RuntimeException("path url is empty.");
        }
        if (Objects.isNull(_baseUrl) || _baseUrl.isBlank()) {
            throw new RuntimeException("path url is empty.");
        }

        PathUrl baseUrl = PathUrl.from(_baseUrl);
        List<SegmentChunk> segmentChunks = SegmentChunkFactory.create(baseUrl);

        PathUrl[] usedPathUrls = new PathUrl[segmentChunks.size()];

        PathUrl requestUrl = PathUrl.from(_requestUrl);
        Stream<PathUrl> requestUrlStream = Stream.of(requestUrl);
        for (int i = 0; i < segmentChunks.size(); i++) {
            SegmentChunk segmentChunk = segmentChunks.get(i);

            int finalI = i;
            requestUrlStream = requestUrlStream
                .map(segmentChunk::consume)
                .flatMap(Collection::stream)
                .peek(usedPathUrl -> usedPathUrls[finalI] = usedPathUrl);
        }
        Optional<PathUrl> optionalAllConsumed = requestUrlStream.filter(PathUrl::isEmtpy).findFirst();

        boolean doesNotAllConsumed = optionalAllConsumed.isEmpty();
        if (doesNotAllConsumed) {
            return Optional.empty();
        }

        PathVariableValue pathVariableValue = getPathVariable(segmentChunks, usedPathUrls);
        return Optional.of(pathVariableValue);
    }

    private static PathVariableValue getPathVariable(List<SegmentChunk> segmentChunks, PathUrl[] usedPath) {
        PathVariableValue pathVariableValue = PathVariableValue.empty();
        for (int i = 0; i < segmentChunks.size(); i++) {
            SegmentChunk segmentChunk = segmentChunks.get(i);

            if (segmentChunk instanceof AbstractPathVariableSegmentChunk) {
                PathUrl pathUrl = usedPath[i];
                Map<PathUrl, PathVariableValue> matchedPathVariables = ((AbstractPathVariableSegmentChunk) segmentChunk).getMatchedPathVariables();

                if (!matchedPathVariables.containsKey(pathUrl)) {
                    throw new RuntimeException("does not exist pathUrl.");
                }

                PathVariableValue matchedPathVariableValue = matchedPathVariables.get(pathUrl);
                pathVariableValue = pathVariableValue.merge(matchedPathVariableValue);
            }
        }
        return pathVariableValue;
    }
}
