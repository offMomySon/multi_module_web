package mapper.segmentv3.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariableUtil;
import mapper.segmentv3.SegmentChunk;
import mapper.segmentv3.WildCardPathVariableSegmentChunk;
import mapper.segmentv3.WildCardSegmentChunk;

public class WildCardSegmentChunkCreateStrategy {
    private static final String WILD_CARD = "/**";

    public static List<SegmentChunk> create(PathUrl _basePathUrl) {
        Objects.requireNonNull(_basePathUrl);

        if (_basePathUrl.isEmtpy()) {
            return Collections.emptyList();
        }

        String baseUrl = _basePathUrl.toValue();
        int wildCardIndex = baseUrl.indexOf(WILD_CARD);
        boolean onlyExistGeneralSegmentChunk = wildCardIndex == -1;
        if (onlyExistGeneralSegmentChunk) {
            return GeneralSegmentChunkCreateCreateStrategy.create(_basePathUrl);
        }

        PathUrl normalPathUrl = PathUrl.from(baseUrl.substring(0, wildCardIndex));
        List<SegmentChunk> normalSegmentChunks = GeneralSegmentChunkCreateCreateStrategy.create(normalPathUrl);

        List<String> wildCardPathUrls = parseWildCardPathUrls(baseUrl, wildCardIndex);
        List<SegmentChunk> wildCardSegmentChunks = createWildCardSegmentChunks(wildCardPathUrls);

        return Stream.concat(normalSegmentChunks.stream(), wildCardSegmentChunks.stream()).collect(Collectors.toUnmodifiableList());
    }

    private static List<SegmentChunk> createWildCardSegmentChunks(List<String> wildCardPathUrls) {
        List<SegmentChunk> segmentChunks = new ArrayList<>();
        for (String wildCardPathUrl : wildCardPathUrls) {
            if (hasPathVariableSegment(wildCardPathUrl)) {
                segmentChunks.add(new WildCardPathVariableSegmentChunk(PathUrl.from(wildCardPathUrl)));
                continue;
            }
            segmentChunks.add(new WildCardSegmentChunk(PathUrl.from(wildCardPathUrl)));
        }
        return segmentChunks;
    }

    private static List<String> parseWildCardPathUrls(String baseUrl, int wildCardIndex) {
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

    private static boolean hasPathVariableSegment(String pathUrl) {
        PathUrl wildCardPathUrl = PathUrl.from(pathUrl);

        while (wildCardPathUrl.doesNotEmpty()) {
            String segment = wildCardPathUrl.popSegment();
            if (PathVariableUtil.isPathVariable(segment)) {
                return true;
            }
        }
        return false;
    }
}
