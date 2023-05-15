package mapper.segment.strategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segment.PathUrl;
import mapper.segment.SegmentChunk;

public class SegmentChunkFactory {
    private static final String WILD_CARD = "/**";

    public static List<SegmentChunk> create(PathUrl basePathUrl) {
        String baseUrl = basePathUrl.toAbsolutePath();
        int wildCardIndex = baseUrl.indexOf(WILD_CARD);
        boolean onlyExistGeneralSegmentChunk = wildCardIndex == -1;
        if (onlyExistGeneralSegmentChunk) {
            return GeneralSegmentChunkCreateCreateStrategy.create(basePathUrl);
        }

        boolean onlyHasWildCardSegmentChunk = wildCardIndex == 0;
        if (onlyHasWildCardSegmentChunk) {
            return WildCardSegmentChunkCreateStrategy.create(basePathUrl);
        }

        PathUrl normalPathUrl = PathUrl.from(baseUrl.substring(0, wildCardIndex));
        List<SegmentChunk> normalSegmentChunks = GeneralSegmentChunkCreateCreateStrategy.create(normalPathUrl);

        PathUrl wildCardPathUrl = PathUrl.from(baseUrl.substring(wildCardIndex));
        List<SegmentChunk> wildCardSegmentChunks = WildCardSegmentChunkCreateStrategy.create(wildCardPathUrl);

        return Stream.concat(normalSegmentChunks.stream(), wildCardSegmentChunks.stream()).collect(Collectors.toList());
    }

}
