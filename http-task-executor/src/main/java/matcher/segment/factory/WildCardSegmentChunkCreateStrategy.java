package matcher.segment.factory;

import matcher.segment.PathVariableUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import matcher.segment.PathUrl2;
import matcher.segment.SegmentChunk;
import matcher.segment.WildCardPathVariableSegmentChunk;
import matcher.segment.WildCardSegmentChunk;

public class WildCardSegmentChunkCreateStrategy {
    private static final String WILD_CARD = "/**";

    public static List<SegmentChunk> create(PathUrl2 _basePathUrl) {
        Objects.requireNonNull(_basePathUrl);

        if (_basePathUrl.isEmpty()) {
            return Collections.emptyList();
        }

        String basePathUrl = _basePathUrl.toAbsolutePath();

        if (!basePathUrl.startsWith(WILD_CARD)) {
            throw new RuntimeException("wildcard 가 처음에 위치해야 합니다.");
        }

        List<String> wildCardPathUrls = splitWildCardPathUrls(basePathUrl);

        return wildCardPathUrls.stream()
            .map(PathUrl2::from)
            .map(pathUrl -> {
                if (hasPathVariableSegment(pathUrl)) {
                    return new WildCardPathVariableSegmentChunk(pathUrl);
                }
                return new WildCardSegmentChunk(pathUrl);
            })
            .collect(Collectors.toUnmodifiableList());
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

    private static boolean hasPathVariableSegment(PathUrl2 wildCardPathUrl) {
        PathUrl2 copiedWildCardPathUrl = wildCardPathUrl.copy();
        while (copiedWildCardPathUrl.doesNotEmpty()) {
            String segment = copiedWildCardPathUrl.popSegment();
            if (PathVariableUtil.isPathVariable(segment)) {
                return true;
            }
        }
        return false;
    }
}
