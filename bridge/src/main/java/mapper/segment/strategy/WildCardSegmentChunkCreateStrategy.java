package mapper.segment.strategy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableUtil;
import mapper.segment.SegmentChunk;
import mapper.segment.WildCardPathVariableSegmentChunk;
import mapper.segment.WildCardSegmentChunk;

public class WildCardSegmentChunkCreateStrategy {
    private static final String WILD_CARD = "/**";

    public static Deque<SegmentChunk> create(PathUrl _basePathUrl) {
        Objects.requireNonNull(_basePathUrl);

        if (_basePathUrl.isEmtpy()) {
            return new ArrayDeque<>();
        }

        String basePathUrl = _basePathUrl.toAbsolutePath();

        if (!basePathUrl.startsWith(WILD_CARD)) {
            throw new RuntimeException("wildcard 가 처음에 위치해야 합니다.");
        }

        List<String> wildCardPathUrls = splitWildCardPathUrls(basePathUrl);

        return wildCardPathUrls.stream()
            .map(PathUrl::from)
            .map(pathUrl -> {
                if (hasPathVariableSegment(pathUrl)) {
                    return new WildCardPathVariableSegmentChunk(pathUrl);
                }
                return new WildCardSegmentChunk(pathUrl);
            })
            .collect(Collectors.toCollection(ArrayDeque::new));
    }

    private static List<String> splitWildCardPathUrls(String baseUrl) {
        int wildCardIndex = 0;
        List<String> wildCardPathUrls = new ArrayList<>();
        while (true) {
            int nextWildCardIndex = baseUrl.indexOf(WILD_CARD, wildCardIndex + 1);
            if (nextWildCardIndex == -1) {
                break;
            }

            String wildCardUrl = baseUrl.substring(wildCardIndex, nextWildCardIndex);
            wildCardPathUrls.add(wildCardUrl);
            wildCardIndex = nextWildCardIndex;
        }
        wildCardPathUrls.add(baseUrl.substring(wildCardIndex));
        return wildCardPathUrls;
    }

    private static boolean hasPathVariableSegment(PathUrl wildCardPathUrl) {
        PathUrl copiedWildCardPathUrl = wildCardPathUrl.copy();
        while (copiedWildCardPathUrl.doesNotEmpty()) {
            String segment = copiedWildCardPathUrl.popSegment();
            if (PathVariableUtil.isPathVariable(segment)) {
                return true;
            }
        }
        return false;
    }
}
