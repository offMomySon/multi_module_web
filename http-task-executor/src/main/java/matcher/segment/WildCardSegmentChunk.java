package matcher.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WildCardSegmentChunk implements SegmentChunk {
    private static final String WILD_CARD = "**";
    private final PathUrl2 baseUrl;

    public WildCardSegmentChunk(PathUrl2 baseUrl) {
        Objects.requireNonNull(baseUrl);

        String segment = baseUrl.peekSegment();
        boolean doesNotWildCard = !WILD_CARD.equals(segment);
        if (doesNotWildCard) {
            throw new RuntimeException("first segment does not wild card.");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl2> consume(PathUrl2 requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl2 copiedBaseUrl = baseUrl.copy();
        PathUrl2 copiedRequestUrl = requestUrl.copy();

        copiedBaseUrl.popSegment();

        boolean onlyHasWildCard = copiedBaseUrl.isEmpty();
        if (onlyHasWildCard) {
            List<PathUrl2> resultPathUrls = new ArrayList<>();

            while (copiedRequestUrl.doesNotEmpty()) {
                resultPathUrls.add(copiedRequestUrl.copy());
                copiedRequestUrl.popSegment();
            }
            resultPathUrls.add(copiedRequestUrl.copy());
            return resultPathUrls;
        }

        PathUrl2 wildCardDeletedBaseUrl = copiedBaseUrl.copy();
        NormalSegmentChunk normalSegmentChunk = new NormalSegmentChunk(wildCardDeletedBaseUrl);

        List<PathUrl2> resultPathUrls = new ArrayList<>();
        while (copiedRequestUrl.doesNotEmpty()) {
            boolean doesNotSufficientRequestSegment = copiedBaseUrl.segmentSize() > copiedRequestUrl.segmentSize();
            if (doesNotSufficientRequestSegment) {
                break;
            }

            List<PathUrl2> consumeResults = normalSegmentChunk.consume(copiedRequestUrl);

            boolean doesNotMatch = consumeResults.isEmpty();
            if (doesNotMatch) {
                copiedRequestUrl.popSegment();
                continue;
            }

            PathUrl2 resultPathUrl = consumeResults.get(0);
            resultPathUrls.add(resultPathUrl);

            copiedRequestUrl.popSegment();
        }

        return resultPathUrls;
    }
}
