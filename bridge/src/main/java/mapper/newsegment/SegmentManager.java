package mapper.newsegment;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.newsegment.chunk.SegmentChunk;
import mapper.newsegment.chunk.SegmentChunk.MatchContext;
import mapper.newsegment.chunk.SegmentChunkCreateStrategy;
import vo.RequestValues;

public class SegmentManager {
    private static final String WILD_CARD = "/\\*\\*";
    private static final String PATH_DELIMITER = "/";

    public static void main(String[] args) {
        String methodPath = "/**/p1/p2/p3/**/p4/p5";
        String requestPath = "/p1/p2/p3/p4/p5";
    }

    public static Optional<RequestValues> match(String methodPath, String requestPath) {
        Queue<SegmentChunk> segmentChunks = SegmentChunkCreateStrategy.create(methodPath);
        List<MatchContext> matchContexts = Stream.of(SegmentProvider.from(requestPath))
            .map(MatchContext::create)
            .collect(Collectors.toUnmodifiableList());

        while (!segmentChunks.isEmpty()) {
            SegmentChunk chunk = segmentChunks.poll();

            matchContexts = matchContexts.stream()
                .map(chunk::match)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
        }

        Optional<MatchContext> optionalFinishContext = matchContexts.stream().filter(MatchContext::finish).findFirst();

        boolean doesNotMatch = optionalFinishContext.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        RequestValues pathVariable = optionalFinishContext.get().getMatchedPathVariable();
        return Optional.of(pathVariable);
    }
}
