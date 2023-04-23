package mapper.newsegment.chunk;

import java.util.List;
import java.util.Objects;
import mapper.newsegment.SegmentProvider;
import vo.RequestValues;

public interface SegmentChunk {
    List<MatchContext> match(MatchContext context);

    class MatchContext {
        private final SegmentProvider nextSegments;
        private final RequestValues matchedPathVariable;

        public MatchContext(SegmentProvider nextSegments, RequestValues matchedPathVariable) {
            Objects.requireNonNull(nextSegments);
            Objects.requireNonNull(matchedPathVariable);
            this.nextSegments = nextSegments;
            this.matchedPathVariable = matchedPathVariable;
        }

        public static MatchContext create(SegmentProvider nextSegments) {
            Objects.requireNonNull(nextSegments);
            return new MatchContext(nextSegments, RequestValues.empty());
        }

        public boolean finish() {
            return nextSegments.isEmpty();
        }

        public SegmentProvider getNextSegments() {
            return nextSegments;
        }

        public RequestValues getMatchedPathVariable() {
            return matchedPathVariable;
        }
    }
}
