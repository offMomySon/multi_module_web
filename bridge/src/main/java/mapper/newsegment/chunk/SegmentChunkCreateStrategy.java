package mapper.newsegment.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class SegmentChunkCreateStrategy {
    private static final String EMPTY_SEGMENT_CHUNK = "";
    private static final String EMPTY_SEGMENT = "";
    private static final String PATH_DELIMITER = "/";
    private static final String WILD_CARD = "**";
    private static final String PATH_VARIABLE_OPENER = "{";
    private static final String PATH_VARIABLE_CLOSER = "}";

    public static Queue<SegmentChunk> create(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            throw new RuntimeException("path is empty.");
        }

        if (PATH_DELIMITER.equals(path)) {
            List<EmptySegmentChunk> emptySegmentChunks = List.of(new EmptySegmentChunk());
            return new ArrayDeque<>(emptySegmentChunks);
        }

        List<String> segmentChunks = splitSegmentChunkByWildCard(path);

        return segmentChunks.stream()
            .map(SegmentChunkCreateStrategy::createSegmentChunk)
            .collect(Collectors.toCollection(ArrayDeque::new));
    }

    private static List<String> splitSegmentChunkByWildCard(String path) {
        boolean onlyHasPathDelimiter = PATH_DELIMITER.equals(path);
        if (onlyHasPathDelimiter) {
            return List.of(EMPTY_SEGMENT_CHUNK);
        }

        path = path.startsWith(PATH_DELIMITER) ? path.substring(1) : path;

        int firstWildCardIndex = path.indexOf(WILD_CARD);
        boolean doesNotExistWildCard = firstWildCardIndex == -1;
        if (doesNotExistWildCard) {
            return List.of(path);
        }

        List<String> segmentChunk = new ArrayList<>();
        if (firstWildCardIndex != 0) {
            String prevChunk = path.substring(0, firstWildCardIndex);
            segmentChunk.add(prevChunk);
        }
        int prevWildCardIndex = firstWildCardIndex;

        for (int index = prevWildCardIndex + 1; index < path.length(); index++) {
            boolean foundWildCard = path.startsWith(WILD_CARD, index);
            if (foundWildCard) {
                String prevChunk = path.substring(prevWildCardIndex, index);
                segmentChunk.add(prevChunk);
                prevWildCardIndex = index;
            }
        }

        String leftSegment = path.substring(prevWildCardIndex);
        segmentChunk.add(leftSegment);

        return segmentChunk;
    }

    private static SegmentChunk createSegmentChunk(String segmentChunk) {
        if (EMPTY_SEGMENT_CHUNK.equals(segmentChunk)) {
            return createSegmentChunk(Collections.emptyList());
        }
        List<String> segments = Arrays.stream(segmentChunk.split(PATH_DELIMITER)).collect(Collectors.toUnmodifiableList());
        return createSegmentChunk(segments);
    }

    public static SegmentChunk createSegmentChunk(List<String> segments) {
        Objects.requireNonNull(segments);

        if (segments.isEmpty()) {
            return new EmptySegmentChunk();
        }

        boolean hasPathVariable = hasPathVariable(segments);
        boolean isFirstSegmentWildCard = segments.get(0).equals(WILD_CARD);

        if (isFirstSegmentWildCard) {
            return new WildCardSegmentChunk(segments);
        }
        if (hasPathVariable) {
            return PathVariableSegmentChunk.from(segments);
        }
        return NormalSegmentChunk.from(segments);
    }

    private static boolean hasPathVariable(List<String> segments) {
        for (String segment : segments) {
            boolean hasPathVariable = segment.startsWith(PATH_VARIABLE_OPENER) && segment.endsWith(PATH_VARIABLE_CLOSER);
            if (hasPathVariable) {
                return true;
            }
        }
        return false;
    }
}
