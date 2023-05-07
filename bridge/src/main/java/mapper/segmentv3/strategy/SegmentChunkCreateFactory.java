package mapper.segmentv3.strategy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.SegmentChunk;

public class SegmentChunkCreateFactory {
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

        boolean onlyHasWildCardSegmentChunk = wildCardIndex == 0;
        if (onlyHasWildCardSegmentChunk) {
            return WildCardSegmentChunkCreateStrategy.create(_basePathUrl);
        }


        PathUrl normalPathUrl = PathUrl.from(baseUrl.substring(0, wildCardIndex));
        List<SegmentChunk> normalSegmentChunks = GeneralSegmentChunkCreateCreateStrategy.create(normalPathUrl);

        PathUrl wildCardPathUrl = PathUrl.from(baseUrl.substring(wildCardIndex));
        List<SegmentChunk> wildCardSegmentChunks = WildCardSegmentChunkCreateStrategy.create(wildCardPathUrl);

        return Stream.concat(normalSegmentChunks.stream(), wildCardSegmentChunks.stream()).collect(Collectors.toUnmodifiableList());
    }

}
