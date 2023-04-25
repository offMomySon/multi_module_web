package mapper.newsegment.chunk;

import java.util.List;
import java.util.Objects;
import mapper.MatchSegment;
import mapper.newsegment.SegmentProvider;

public interface SegmentChunk {
    List<Result> consume(SegmentProvider segmentProvider);

    class Result {
        private final MatchSegment matchSegment;
        private final SegmentProvider leftSegments;

        public Result(MatchSegment matchSegment, SegmentProvider leftSegments) {
            Objects.requireNonNull(matchSegment);
            Objects.requireNonNull(leftSegments);

            this.matchSegment = matchSegment;
            this.leftSegments = leftSegments;
        }

        public static Result empty() {
            return new Result(MatchSegment.empty(), SegmentProvider.empty());
        }

        public MatchSegment getMatchSegment() {
            return matchSegment;
        }

        public SegmentProvider getLeftSegments() {
            return leftSegments;
        }

        @Override
        public String toString() {
            return "Result{" +
                "matchSegment=" + matchSegment +
                ", leftSegments=" + leftSegments +
                '}';
        }
    }
}
