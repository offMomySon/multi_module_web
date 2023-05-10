package mapper.segment.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segment.PathUrl;
import mapper.segment.SegmentChunk;

public class SegmentChunkFactory {
    private static final String WILD_CARD = "/**";

    public static Deque<SegmentChunk> create(PathUrl _basePathUrl) {
        Objects.requireNonNull(_basePathUrl);

        String baseUrl = _basePathUrl.toAbsolutePath();
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
        Deque<SegmentChunk> normalSegmentChunks = GeneralSegmentChunkCreateCreateStrategy.create(normalPathUrl);

        PathUrl wildCardPathUrl = PathUrl.from(baseUrl.substring(wildCardIndex));
        Deque<SegmentChunk> wildCardSegmentChunks = WildCardSegmentChunkCreateStrategy.create(wildCardPathUrl);

        return Stream.concat(normalSegmentChunks.stream(), wildCardSegmentChunks.stream()).collect(Collectors.toCollection(ArrayDeque::new));
    }

}
