package matcher.segment.factory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import matcher.segment.PathUrl2;
import matcher.segment.SegmentChunk;

public class SegmentChunkFactory {
    private static final String WILD_CARD = "/**";

    private final PathUrl2 basePathUrl;

    public SegmentChunkFactory(PathUrl2 basePathUrl) {
        Objects.requireNonNull(basePathUrl);
        this.basePathUrl = basePathUrl;
    }

    public List<SegmentChunk> create() {
        String baseUrl = basePathUrl.toAbsolutePath();
        int wildCardIndex = baseUrl.indexOf(WILD_CARD);
        boolean onlyExistGeneralSegmentChunk = wildCardIndex == -1;
        if (onlyExistGeneralSegmentChunk) {
            return GeneralSegmentChunkCreateStrategy.create(basePathUrl);
        }

        boolean onlyHasWildCardSegmentChunk = wildCardIndex == 0;
        if (onlyHasWildCardSegmentChunk) {
            return WildCardSegmentChunkCreateStrategy.create(basePathUrl);
        }

        PathUrl2 normalPathUrl = PathUrl2.from(baseUrl.substring(0, wildCardIndex));
        List<SegmentChunk> normalSegmentChunks = GeneralSegmentChunkCreateStrategy.create(normalPathUrl);

        PathUrl2 wildCardPathUrl = PathUrl2.from(baseUrl.substring(wildCardIndex));
        List<SegmentChunk> wildCardSegmentChunks = WildCardSegmentChunkCreateStrategy.create(wildCardPathUrl);

        return Stream.concat(normalSegmentChunks.stream(), wildCardSegmentChunks.stream()).collect(Collectors.toList());
    }
}
