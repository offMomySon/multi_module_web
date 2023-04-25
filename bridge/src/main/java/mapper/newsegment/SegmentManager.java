package mapper.newsegment;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.MatchSegment;
import mapper.newsegment.chunk.SegmentChunk;
import mapper.newsegment.chunk.SegmentChunk.MatchResult;
import mapper.newsegment.chunk.SegmentChunkCreateStrategy;
import vo.RequestValues;

public class SegmentManager {
    public static Optional<RequestValues> doMatch(String methodPath, String requestPath) {
        Queue<SegmentChunk> segmentChunks = SegmentChunkCreateStrategy.createSegmentChunks(methodPath);
        List<MatchContext> matchContexts = Stream.of(SegmentProvider.from(requestPath))
            .map(MatchContext::create)
            .collect(Collectors.toUnmodifiableList());

        while (!segmentChunks.isEmpty()) {
            SegmentChunk chunk = segmentChunks.poll();

            matchContexts = matchContexts.stream()
                .map(context -> doMatch(chunk, context))
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

    private static List<MatchContext> doMatch(SegmentChunk chunk, MatchContext context) {
        SegmentProvider segmentProvider = context.getProvider();
        List<MatchResult> matchResults = chunk.match(segmentProvider);

        return matchResults.stream()
            .map(result -> createNextMatchContext(context, result))
            .collect(Collectors.toUnmodifiableList());
    }

    private static MatchContext createNextMatchContext(MatchContext context, MatchResult matchResult) {
        SegmentProvider nextSegments = matchResult.getLeftSegments();

        MatchSegment prevMatchSegment = context.getMatchSegment();
        MatchSegment resultMatchSegment = matchResult.getMatchSegment();
        MatchSegment mergeMatchSegment = prevMatchSegment.merge(resultMatchSegment);

        return new MatchContext(nextSegments, mergeMatchSegment);
    }
}
