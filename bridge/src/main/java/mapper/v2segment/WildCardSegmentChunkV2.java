package mapper.v2segment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import mapper.newsegment.SegmentProvider;

public class WildCardSegmentChunkV2 implements SegmentChunkV2 {
    private static final String WILD_CARD = "**";
    private final List<String> segments;

    public WildCardSegmentChunkV2(List<String> segments) {
        if (Objects.isNull(segments) || segments.isEmpty()) {
            throw new RuntimeException("does not exist segments");
        }

        String firstSegments = segments.get(0);
        boolean doesNotWildcardAtFistSegment = !Objects.equals(WILD_CARD, firstSegments);
        if (doesNotWildcardAtFistSegment) {
            throw new RuntimeException("at first segment, does not wild card.");
        }

        this.segments = segments;
    }

    @Override
    public List<SegmentProvider> match(SegmentProvider provider) {
        if (Objects.isNull(provider)) {
            throw new RuntimeException("provider is null.");
        }

        SegmentProvider otherProvider = provider.copy();
        List<String> thisSegment = segments.subList(1, segments.size());

        
        boolean doesNotSufficientProvider = otherProvider.size() < thisSegment.size();
        if (doesNotSufficientProvider) {
            return Collections.emptyList();
        }

        for (int index = 0; index < thisSegment.size(); index++) {


        }


        return null;
    }
}
