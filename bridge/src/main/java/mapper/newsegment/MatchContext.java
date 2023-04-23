package mapper.newsegment;

import java.util.Objects;
import mapper.MatchSegment;

public class MatchContext {
    private final SegmentProvider provider;
    private final MatchSegment matchSegment;

    public MatchContext(SegmentProvider provider, MatchSegment matchSegment) {
        Objects.requireNonNull(provider);
        Objects.requireNonNull(matchSegment);

        this.provider = provider;
        this.matchSegment = matchSegment;
    }

    public static MatchContext create(SegmentProvider provider) {
        Objects.requireNonNull(provider);
        return new MatchContext(provider, MatchSegment.empty());
    }

    public boolean finish() {
        return provider.isEmpty();
    }

    public SegmentProvider getProvider() {
        return provider;
    }

    public MatchSegment getMatchSegment() {
        return matchSegment;
    }
}
