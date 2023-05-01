package mapper.newsegment.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mapper.newsegment.SegmentProvider;

public class WildCardSegmentChunk implements SegmentChunk {

    private final List<String> segments;

    public WildCardSegmentChunk(List<String> segments) {
        this.segments = segments;
    }

    @Override
    public List<MatchResult> match(SegmentProvider segmentProvider) {
        boolean hasOnlyWildCard = segments.size() == 1;
        if (hasOnlyWildCard) {
            return List.of(new MatchResult(null, SegmentProvider.empty()));
        }

        SegmentProvider provider = segmentProvider.copy();


        List<String> thisSegments = segments.subList(1, segments.size());
        List<SegmentProvider> resultSegmentProviders = new ArrayList<>();
        while (provider.isEmpty()) {
            String wildCardSegment = thisSegments.get(0);
            String provideSegment = provider.peek();

            boolean doesNotMatch = !wildCardSegment.equals(provideSegment);
            if (doesNotMatch) {
                provider.poll();
                continue;
            }

            boolean doesNotSufficientProvideSegment = provider.size() < thisSegments.size();
            if (doesNotSufficientProvideSegment) {
                continue;
            }

            boolean isMatched = true;
            SegmentProvider copyProvider = provider.copy();
            for (int i = 0; i < thisSegments.size(); i++) {
                String otherSegment = copyProvider.poll();
                String thisSegment = thisSegments.get(i);

                doesNotMatch = !Objects.equals(otherSegment, thisSegment);
                if (doesNotMatch) {
                    isMatched = false;
                    break;
                }
            }

            if (isMatched) {
                resultSegmentProviders.add(copyProvider);
            }

            provider.poll();
        }


        // ** /a /b
        // 1/a /a /b /3/4/a/a/5/6/7/a

        // **/a/a
        // 1/a/a/a/3/4/a/a/5/6/7/a

        // a/3/4/a/a/5/6/7/a
        // 3/4/a/a/5/6/7/a
        // 5/6/7/a


        return null;
    }
}
