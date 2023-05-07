package mapper.segmentv3.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariableUtil;
import mapper.segmentv3.SegmentChunk;
import mapper.segmentv3.WildCardPathVariableSegmentChunk;
import mapper.segmentv3.WildCardSegmentChunk;

public class WildCardSegmentChunkCreateStrategy {
    private static final String WILD_CARD = "/\\*\\*";

    public static List<SegmentChunk> create(PathUrl basePathUrl) {
        Objects.requireNonNull(basePathUrl);

        if (basePathUrl.isEmtpy()) {
            return Collections.emptyList();
        }

        List<String> splitPathUrls = Arrays.stream(basePathUrl.toValue().split(WILD_CARD)).collect(Collectors.toUnmodifiableList());

        PathUrl normalPathUrl = PathUrl.from(splitPathUrls.get(0));
        List<SegmentChunk> normalSegmentChunks = GeneralSegmentChunkCreateCreateStrategy.create(normalPathUrl);
        List<SegmentChunk> segmentChunks = new ArrayList<>(normalSegmentChunks);

        List<String> wildCardPathUrls = splitPathUrls.subList(1, splitPathUrls.size()).stream()
            .map(url -> "/**" + url)
            .collect(Collectors.toUnmodifiableList());
        for (String wildCardPathUrl : wildCardPathUrls) {
            if (hasPathVariableSegment(wildCardPathUrl)) {
                segmentChunks.add(new WildCardPathVariableSegmentChunk(PathUrl.from(wildCardPathUrl)));
                continue;
            }
            segmentChunks.add(new WildCardSegmentChunk(PathUrl.from(wildCardPathUrl)));
        }

        return segmentChunks;
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
