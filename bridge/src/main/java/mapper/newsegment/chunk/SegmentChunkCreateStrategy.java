package mapper.newsegment.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class SegmentChunkCreateStrategy {
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

        List<String> segmentChunks = splitSegmentChunks(path);

        return segmentChunks.stream()
            .map(SegmentChunkCreateStrategy::getSegmentChunk)
            .collect(Collectors.toCollection(ArrayDeque::new));
    }

    private static List<String> splitSegmentChunks(String path) {
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

    private static SegmentChunk getSegmentChunk(String segmentChunk) {
        String[] segments = segmentChunk.split(PATH_DELIMITER);

        boolean hasPathVariable = hasPathVariable(segments);
        boolean hasWildCard = segmentChunk.startsWith(WILD_CARD);

        if (hasWildCard) {
            return new WildCardSegmentChunk();
        }
        if (hasPathVariable) {
            return new PathVariableSegmentChunk();
        }
        return new NormalSegmentChunk();
    }

    private static boolean hasPathVariable(String[] segments) {
        for (String segment : segments) {
            boolean hasPathVariable = segment.startsWith(PATH_VARIABLE_OPENER) && segment.endsWith(PATH_VARIABLE_CLOSER);
            if (hasPathVariable) {
                return true;
            }
        }
        return false;
    }
}
