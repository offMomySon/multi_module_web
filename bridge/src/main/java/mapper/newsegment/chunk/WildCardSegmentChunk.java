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
        this.segments = segments.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    public static WildCardSegmentChunk from(String... segments) {
        Objects.requireNonNull(segments);
        return new WildCardSegmentChunk(Arrays.stream(segments).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public List<Result> consume(SegmentProvider provider) {
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
            System.out.println("only");
            return wildCardMatches.stream()
                .map(wildCardMatch -> {
                    List<String> wildCardMatchSegments = wildCardMatch.getMatchSegments();
                    String newWildCardMatchSegments = String.join("/", wildCardMatchSegments);
                    MatchSegment wildCardMatchSegment = new MatchSegment(Map.of(WILD_CARD, newWildCardMatchSegments));

                    List<String> doesNotMatchSegments = wildCardMatch.doesNotMatchSegments;
                    SegmentProvider leftSegments = SegmentProvider.from(doesNotMatchSegments);

                    return new Result(wildCardMatchSegment, leftSegments);
                })
                .collect(Collectors.toUnmodifiableList());
        }

        System.out.println("does not only");
        SegmentChunk wildCardExcludeChunk = SegmentChunkCreateStrategy.createSegmentChunk(wildCardExcludeSegments);
        System.out.println(wildCardExcludeChunk);

        return wildCardMatches.stream()
            .map(wildCardMatch -> {
                List<String> doesNotWildCardMatchSegments = wildCardMatch.getDoesNotMatchSegments();
                SegmentProvider segmentProvider = SegmentProvider.from(doesNotWildCardMatchSegments);
                List<Result> results = wildCardExcludeChunk.consume(segmentProvider);

                List<String> wildCardMatchSegments = wildCardMatch.getMatchSegments();
                String newWildCardMatchSegments = String.join("/", wildCardMatchSegments);
                MatchSegment wildCardMatchSegment = new MatchSegment(Map.of(WILD_CARD, newWildCardMatchSegments));

                return mergeWildCardMatchSegment(results, wildCardMatchSegment);
            })
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<Result> mergeWildCardMatchSegment(List<Result> results, MatchSegment wildCardMatchSegment) {
        return results.stream()
            .map(result -> {
                MatchSegment matchSegment = result.getMatchSegment();
                MatchSegment newMatchSegment = matchSegment.merge(wildCardMatchSegment);

                SegmentProvider leftSegments = result.getLeftSegments();
                return new Result(newMatchSegment, leftSegments);
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
