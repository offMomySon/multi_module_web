package mapper.newsegment.chunk;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;

public class WildCardSegmentChunk implements SegmentChunk {
    private static final String WILD_CARD = "**";

    private final List<String> segments;

    public WildCardSegmentChunk(List<String> segments) {
        Objects.requireNonNull(segments);

        boolean doseNotHasAnyWildCard = segments.stream().noneMatch(WILD_CARD::equals);
        if (doseNotHasAnyWildCard) {
            throw new RuntimeException("does not has any Wild card.");
        }

        this.segments = segments.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    public static WildCardSegmentChunk from(String... segments) {
        Objects.requireNonNull(segments);
        return new WildCardSegmentChunk(Arrays.stream(segments).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public List<MatchResult> match(SegmentProvider provider) {
        Objects.requireNonNull(provider);

        List<String> newProvider = provider.toList();

        List<WildCardMatch> wildCardMatches = IntStream.rangeClosed(0, newProvider.size()).boxed()
            .map(partitionIndex -> {
                List<String> wildCardMatchSegments = newProvider.subList(0, partitionIndex);
                List<String> doesNotMatchSegments = newProvider.subList(partitionIndex, newProvider.size());
                return new WildCardMatch(wildCardMatchSegments, doesNotMatchSegments);
            })
            .collect(Collectors.toUnmodifiableList());

        List<String> wildCardExcludeSegments = segments.subList(1, segments.size());

        boolean onlyWildCard = wildCardExcludeSegments.isEmpty();
        if (onlyWildCard) {
            return wildCardMatches.stream()
                .map(wildCardMatch -> {
                    List<String> doesNotMatchSegments = wildCardMatch.doesNotMatchSegments;
                    SegmentProvider leftSegments = SegmentProvider.from(doesNotMatchSegments);

                    MatchSegment wildCardMatchSegment = createWildCardSegment(wildCardMatch);
                    return new MatchResult(wildCardMatchSegment, leftSegments);
                })
                .collect(Collectors.toUnmodifiableList());
        }

        SegmentChunk wildCardExcludeChunk = SegmentChunkCreateStrategy.createSegmentChunk(wildCardExcludeSegments);

        return wildCardMatches.stream()
            .map(wildCardMatch -> {
                List<String> doesNotWildCardMatchSegments = wildCardMatch.getDoesNotMatchSegments();
                SegmentProvider segmentProvider = SegmentProvider.from(doesNotWildCardMatchSegments);
                List<MatchResult> matchResults = wildCardExcludeChunk.match(segmentProvider);

                MatchSegment wildCardMatchSegment = createWildCardSegment(wildCardMatch);
                return mergeWildCardMatchSegment(matchResults, wildCardMatchSegment);
            })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }

    private static MatchSegment createWildCardSegment(WildCardMatch wildCardMatch) {
        List<String> wildCardMatchSegments = wildCardMatch.getMatchSegments();
        String newWildCardMatchSegments = String.join("/", wildCardMatchSegments);
        return new MatchSegment(Map.of(WILD_CARD, newWildCardMatchSegments));
    }

    private static List<MatchResult> mergeWildCardMatchSegment(List<MatchResult> matchResults, MatchSegment wildCardMatchSegment) {
        return matchResults.stream()
            .map(result -> {
                MatchSegment matchSegment = result.getMatchSegment();
                MatchSegment newMatchSegment = matchSegment.merge(wildCardMatchSegment);

                SegmentProvider leftSegments = result.getLeftSegments();
                return new MatchResult(newMatchSegment, leftSegments);
            })
            .collect(Collectors.toUnmodifiableList());
    }

    private static class WildCardMatch {
        private final List<String> matchSegments;
        private final List<String> doesNotMatchSegments;

        public WildCardMatch(List<String> matchSegments, List<String> doesNotMatchSegments) {
            this.matchSegments = matchSegments;
            this.doesNotMatchSegments = doesNotMatchSegments;
        }

        public List<String> getMatchSegments() {
            return matchSegments;
        }

        public List<String> getDoesNotMatchSegments() {
            return doesNotMatchSegments;
        }
    }

}
