package matcher.segment.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import matcher.path.PathUrl;
import matcher.path.PathUtil;
import matcher.segment.SegmentChunk;
import matcher.segment.WildCardPathVariableSegmentChunk;
import matcher.segment.WildCardSegmentChunk;
import static java.util.Objects.isNull;

public class WildCardSegmentChunkCreateStrategy {
    private static final String WILD_CARD = "/**";

    public static List<SegmentChunk> create(PathUrl _basePathUrl) {
        if (isNull(_basePathUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

        if (_basePathUrl.isEmpty()) {
            return Collections.emptyList();
        }

        String basePathUrl = _basePathUrl.toAbsolutePath();
        if (!basePathUrl.startsWith(WILD_CARD)) {
            throw new RuntimeException("wildcard 가 처음에 위치해야 합니다.");
        }

        List<String> wildCardPathUrls = splitWildCardPathUrls(basePathUrl);

        return wildCardPathUrls.stream()
            .map(PathUrl::of)
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

    private static boolean hasPathVariableSegment(PathUrl wildCardPathUrl) {
        PathUrl copiedWildCardPathUrl = wildCardPathUrl.copy();
        while (copiedWildCardPathUrl.doesNotEmpty()) {
            String segment = copiedWildCardPathUrl.popSegment();
            if (PathUtil.isPathVariable(segment)) {
                return true;
            }
        }
        return false;
    }
}