package mapper.segmentv3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import mapper.segmentv3.pathvariable.AbstractPathVariableSegmentChunk;
import mapper.segmentv3.strategy.SegmentChunkCreateFactory;

public class SegmentManager {
    public static Optional<PathVariable> consume(String _baseUrl, String _requestUrl) {
        if (Objects.isNull(_requestUrl) || _requestUrl.isBlank()) {
            throw new RuntimeException("path url is empty.");
        }
        if (Objects.isNull(_baseUrl) || _baseUrl.isBlank()) {
            throw new RuntimeException("path url is empty.");
        }

        PathUrl baseUrl = PathUrl.from(_baseUrl);
        List<SegmentChunk> segmentChunks = SegmentChunkCreateFactory.create(baseUrl);

        PathUrl requestUrl = PathUrl.from(_requestUrl);
        PathUrl[] usedPathUrls = new PathUrl[segmentChunks.size()];
        Stream<PathUrl> requestUrlStream = new ArrayList<>(List.of(requestUrl)).stream();
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

        PathVariable pathVariable = getPathVariable(segmentChunks, usedPathUrls);
        return Optional.of(pathVariable);
    }

    private static PathVariable getPathVariable(List<SegmentChunk> segmentChunks, PathUrl[] usedPath) {
        PathVariable pathVariable = PathVariable.empty();
        for (int i = 0; i < segmentChunks.size(); i++) {
            SegmentChunk segmentChunk = segmentChunks.get(i);

            if (segmentChunk instanceof AbstractPathVariableSegmentChunk) {
                PathUrl pathUrl = usedPath[i];
                Map<PathUrl, PathVariable> matchedPathVariables = ((AbstractPathVariableSegmentChunk) segmentChunk).getMatchedPathVariables();

                if (!matchedPathVariables.containsKey(pathUrl)) {
                    throw new RuntimeException("does not exist pathUrl.");
                }

                PathVariable matchedPathVariable = matchedPathVariables.get(pathUrl);
                pathVariable.merge(matchedPathVariable);
            }
        }
        return pathVariable;
    }
}
