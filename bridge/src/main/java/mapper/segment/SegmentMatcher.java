package mapper.segment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import vo.RequestValues;

@Slf4j
public class SegmentMatcher {
    private static final String WILD_CARD_PATTERN = "**";
    private static final String PATH_DELIMITER = "/";

    private final List<Segment> segments;

    private SegmentMatcher(List<Segment> segments) {
        this.segments = segments.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
    }

    public static SegmentMatcher from(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            throw new IllegalArgumentException("path is empty.");
        }

        boolean isRootPath = PATH_DELIMITER.equals(path);
        if (isRootPath) {
            return new SegmentMatcher(Collections.emptyList());
        }

        List<String> segments = splitAsSegment(path);

        long wildCardCount = segments.stream().filter(WILD_CARD_PATTERN::equals).count();
        boolean hasMoreThanOneWildCard = wildCardCount >= 2;
        if (hasMoreThanOneWildCard) {
            throw new IllegalArgumentException("path 에 2개 이상의 wildcard pattern 이 존재하면 생성할 수 없습니다.");
        }

        boolean doesNotWildCardPositionAtFirst = !segments.get(0).equals(WILD_CARD_PATTERN) && wildCardCount == 1;
        if (doesNotWildCardPositionAtFirst) {
            throw new IllegalArgumentException("wildcard pattern 은 첫번째 segment 에 존재해야합니다.");
        }

        return new SegmentMatcher(segments.stream()
                                      .map(Segment::create)
                                      .collect(Collectors.toUnmodifiableList()));
    }

    public List<MatchResult> match(String otherPath) {
        if (Objects.isNull(otherPath)) {
            throw new IllegalArgumentException("path is empty.");
        }

        List<String> otherSegments = splitAsSegment(otherPath);

        boolean bothPathRoot = this.segments.isEmpty() && otherSegments.isEmpty();
        if (bothPathRoot) {
            return List.of(MatchResult.empty());
        }

        boolean onlyThisPathRoot = this.segments.isEmpty();
        if (onlyThisPathRoot) {
            return Collections.emptyList();
        }

        Segment firstSegment = this.segments.get(0);
        boolean doesNotWildCardFirst = firstSegment instanceof DefaultSegment || firstSegment instanceof PathVariableSegment;
        if (doesNotWildCardFirst) {
            int chunkSize = this.segments.size();
            boolean doesNotPossibleChunk = otherSegments.size() < chunkSize;
            if (doesNotPossibleChunk) {
                return Collections.emptyList();
            }

            SegmentChunk segmentChunk = createSegmentChunk(otherSegments, 0, chunkSize);
            List<String> segmentsForCompare = segmentChunk.getSegmentsForCompare();

            if (doesNotAllMatchSegment(this.segments, segmentsForCompare)) {
                return Collections.emptyList();
            }
            return List.of(createMatchResult(this.segments, segmentChunk));
        }

        List<Segment> segmentsExcludeWildCard = this.segments.subList(1, this.segments.size());
        int chunkSize = segmentsExcludeWildCard.size();
        boolean doesNotPossibleChunk = otherSegments.size() < chunkSize;
        if (doesNotPossibleChunk) {
            return Collections.emptyList();
        }

        int lastPossibleChunkStartIndex = otherSegments.size() - chunkSize;
        List<SegmentChunk> segmentChunks = IntStream.rangeClosed(0, lastPossibleChunkStartIndex)
            .mapToObj(chunkStartIndex -> createSegmentChunk(otherSegments, chunkStartIndex, chunkSize))
            .collect(Collectors.toUnmodifiableList());

        return segmentChunks.stream()
            .filter(segmentChunk -> allMatchSegment(segmentsExcludeWildCard, segmentChunk.getSegmentsForCompare()))
            .map(segmentChunk -> createMatchResult(segmentsExcludeWildCard, segmentChunk))
            .collect(Collectors.toUnmodifiableList());
    }

    private static MatchResult createMatchResult(List<Segment> thisSegments, SegmentChunk segmentChunk) {
        List<String> segmentsForCompare = segmentChunk.getSegmentsForCompare();
        RequestValues pathVariable = extractPathVariable(thisSegments, segmentsForCompare);
        String leftPath = segmentChunk.getLeftPath();
        return new MatchResult(leftPath, pathVariable);
    }

    private static List<String> splitAsSegment(String path) {
        List<String> segments = Arrays.asList(path.split(PATH_DELIMITER));
        if (segments.isEmpty()) {
            return segments;
        }
        return segments.subList(1, segments.size());
    }

    private static boolean doesNotAllMatchSegment(List<Segment> segments, List<String> compareSegments) {
        return !allMatchSegment(segments, compareSegments);
    }

    private static boolean allMatchSegment(List<Segment> segments, List<String> otherSegments) {
        for (int i = 0; i < otherSegments.size(); i++) {
            Segment segment = segments.get(i);
            String otherSegment = otherSegments.get(i);

            if (segment.doesNotMatch(otherSegment)) {
                return false;
            }
        }
        return true;
    }

    private static RequestValues extractPathVariable(List<Segment> segments, List<String> otherSegments) {
        Map<String, String> pathVariable = new HashMap<>();
        for (int i = 0; i < otherSegments.size(); i++) {
            Segment segment = segments.get(i);

            if (!(segment instanceof PathVariableSegment)) {
                continue;
            }

            PathVariableSegment pathVariableSegment = (PathVariableSegment) segment;

            String key = pathVariableSegment.getExtractBraceKey();
            String otherSegment = otherSegments.get(i);
            pathVariable.put(key, otherSegment);
        }
        return new RequestValues(pathVariable);
    }

    private static SegmentChunk createSegmentChunk(List<String> segments, int startIndex, int size) {
        List<String> segmentsForCompare = new ArrayList<>();
        for (int i = startIndex; i < startIndex + size; i++) {
            String segment = segments.get(i);
            segmentsForCompare.add(segment);
        }

        List<String> leftSegments = new ArrayList<>();
        for (int i = startIndex + size; i < segments.size(); i++) {
            String segment = segments.get(i);
            leftSegments.add(segment);
        }

        return new SegmentChunk(segmentsForCompare, leftSegments);
    }

    public static class MatchResult {
        private final String leftPath;
        private final RequestValues pathVariable;

        public MatchResult(String leftPath, RequestValues pathVariable) {
            Objects.requireNonNull(leftPath);
            Objects.requireNonNull(pathVariable);

            this.leftPath = leftPath;
            this.pathVariable = pathVariable;
        }

        public boolean isFinish() {
            return leftPath.isBlank();
        }

        public boolean doesNotFinish() {
            return !isFinish();
        }

        public static MatchResult empty() {
            return new MatchResult("", RequestValues.empty());
        }

        public String getLeftPath() {
            return leftPath;
        }

        public RequestValues getPathVariable() {
            return pathVariable;
        }
    }

    private static class SegmentChunk {
        private final List<String> segmentsForCompare;
        private final List<String> leftSegments;

        public SegmentChunk(List<String> segmentsForCompare, List<String> leftSegments) {
            Objects.requireNonNull(segmentsForCompare);
            Objects.requireNonNull(leftSegments);

            this.segmentsForCompare = segmentsForCompare;
            this.leftSegments = leftSegments;
        }

        public List<String> getSegmentsForCompare() {
            return segmentsForCompare;
        }

        public List<String> getLeftSegments() {
            return leftSegments;
        }

        public String getLeftPath() {
            String pathBehind;
            if (leftSegments.size() == 0) {
                pathBehind = "";
            } else {
                pathBehind = leftSegments.stream().collect(Collectors.joining(PATH_DELIMITER, PATH_DELIMITER, ""));
            }
            return pathBehind;
        }
    }
}