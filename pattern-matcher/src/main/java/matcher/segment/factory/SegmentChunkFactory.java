package matcher.segment.factory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import matcher.segment.path.PathUrl;
import matcher.segment.SegmentChunk;
import static java.util.Objects.isNull;

public class SegmentChunkFactory {
    private static final String WILD_CARD = "/**";

    public static List<SegmentChunk> create(PathUrl basePathUrl) {
        if (isNull(basePathUrl)) {
            throw new RuntimeException("Must parameter not be null.");
        }

        String baseUrl = basePathUrl.toAbsolutePath();
        int wildCardIndex = baseUrl.indexOf(WILD_CARD);
        boolean onlyExistGeneralSegmentChunk = wildCardIndex == -1;
        if (onlyExistGeneralSegmentChunk) {
            SegmentChunk generalSegmentChunk = GeneralSegmentChunkCreateStrategy.create(basePathUrl);
            return List.of(generalSegmentChunk);
        }

        boolean onlyHasWildCardSegmentChunk = wildCardIndex == 0;
        if (onlyHasWildCardSegmentChunk) {
            return WildCardSegmentChunkCreateStrategy.create(basePathUrl);
        }

        PathUrl normalPathUrl = PathUrl.from(baseUrl.substring(0, wildCardIndex));
        SegmentChunk generalSegmentChunk = GeneralSegmentChunkCreateStrategy.create(normalPathUrl);

        PathUrl wildCardPathUrl = PathUrl.from(baseUrl.substring(wildCardIndex));
        List<SegmentChunk> wildCardSegmentChunks = WildCardSegmentChunkCreateStrategy.create(wildCardPathUrl);

        return Stream.concat(Stream.of(generalSegmentChunk), wildCardSegmentChunks.stream()).collect(Collectors.toList());
    }
}