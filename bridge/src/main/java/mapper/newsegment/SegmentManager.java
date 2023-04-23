package mapper.newsegment;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.MatchSegment;
import mapper.newsegment.chunk.SegmentChunk;
import mapper.newsegment.chunk.SegmentChunk.Result;
import mapper.newsegment.chunk.SegmentChunkCreateStrategy;
import vo.RequestValues;

public class SegmentManager {
    private static final String WILD_CARD = "/\\*\\*";
    private static final String PATH_DELIMITER = "/";

    public static void main(String[] args) {
        String methodPath = "/**/p1/p2/p3/**/p4/p5";
        String requestPath = "/p1/p2/p3/p4/p5";
    }

    public static Optional<RequestValues> consume(String methodPath, String requestPath) {
        Queue<SegmentChunk> segmentChunks = SegmentChunkCreateStrategy.create(methodPath);
        List<MatchContext> matchContexts = Stream.of(SegmentProvider.from(requestPath))
            .map(MatchContext::create)
            .collect(Collectors.toUnmodifiableList());

        while (!segmentChunks.isEmpty()) {
            SegmentChunk chunk = segmentChunks.poll();

            matchContexts = matchContexts.stream()
                .map(context -> consume(chunk, context))
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
        }

        Optional<MatchContext> optionalFinishContext = matchContexts.stream().filter(MatchContext::finish).findFirst();

        boolean doesNotMatch = optionalFinishContext.isEmpty();
        if (doesNotMatch) {
            return Optional.empty();
        }

        RequestValues pathVariable = optionalFinishContext.get().getMatchSegment().extractPathVariable();
        return Optional.of(pathVariable);
    }

    private static List<MatchContext> consume(SegmentChunk chunk, MatchContext context) {
        SegmentProvider segmentProvider = context.getProvider();
        List<Result> results = chunk.consume(segmentProvider);

        return results.stream()
            .map(result -> getNextMatchContext(context, result))
            .collect(Collectors.toUnmodifiableList());
    }

    private static MatchContext getNextMatchContext(MatchContext context, Result result) {
        SegmentProvider nextSegment = result.getLeftSegments();

        MatchSegment prevMatchSegment = context.getMatchSegment();
        MatchSegment resultMatchSegment = result.getMatchSegment();
        MatchSegment mergeMatchSegment = prevMatchSegment.merge(resultMatchSegment);

        return new MatchContext(nextSegment, mergeMatchSegment);
    }
}
