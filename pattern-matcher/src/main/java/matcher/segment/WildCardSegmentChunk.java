package matcher.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import matcher.segment.path.PathUrl;
import static java.util.Objects.isNull;

public class WildCardSegmentChunk implements SegmentChunk {
    private static final String WILD_CARD = "**";
    private final PathUrl baseUrl;

    public WildCardSegmentChunk(PathUrl baseUrl) {
        if (isNull(baseUrl)) {
            throw new RuntimeException("Must parameter not be null.");
        }

        String segment = baseUrl.peekSegment();
        boolean doesNotWildCard = !WILD_CARD.equals(segment);
        if (doesNotWildCard) {
            throw new RuntimeException("first segment does not wild card.");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl> consume(PathUrl requestUrl) {
        if (isNull(requestUrl)) {
            throw new RuntimeException("Must parameter not be null.");
        }

        PathUrl copiedBaseUrl = baseUrl.copy();
        PathUrl copiedRequestUrl = requestUrl.copy();

        copiedBaseUrl.popSegment();

        boolean onlyHasWildCard = copiedBaseUrl.isEmpty();
        if (onlyHasWildCard) {
            List<PathUrl> resultPathUrls = new ArrayList<>();

            while (copiedRequestUrl.doesNotEmpty()) {
                resultPathUrls.add(copiedRequestUrl.copy());
                copiedRequestUrl.popSegment();
            }
            resultPathUrls.add(copiedRequestUrl.copy());
            return resultPathUrls;
        }

        PathUrl wildCardDeletedBaseUrl = copiedBaseUrl.copy();
        NormalSegmentChunk normalSegmentChunk = new NormalSegmentChunk(wildCardDeletedBaseUrl);

        List<PathUrl> resultPathUrls = new ArrayList<>();
        while (copiedRequestUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestSegment = copiedBaseUrl.segmentSize() > copiedRequestUrl.segmentSize();
            if (doesNotSufficientRequestSegment) {
                break;
            }

            List<PathUrl> consumeResults = normalSegmentChunk.consume(copiedRequestUrl);

            boolean doesNotMatch = consumeResults.isEmpty();
            if (doesNotMatch) {
                copiedRequestUrl.popSegment();
                continue;
            }

            PathUrl resultPathUrl = consumeResults.get(0);
            resultPathUrls.add(resultPathUrl);

            copiedRequestUrl.popSegment();
        }

        return resultPathUrls;
    }
}