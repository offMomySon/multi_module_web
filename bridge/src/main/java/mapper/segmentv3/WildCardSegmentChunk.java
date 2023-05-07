package mapper.segmentv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WildCardSegmentChunk implements SegmentChunk {
    private static final String WILD_CARD = "**";
    private final PathUrl baseUrl;

    public WildCardSegmentChunk(PathUrl baseUrl) {
        Objects.requireNonNull(baseUrl);

        String segment = baseUrl.peekSegment();
        boolean doesNotWildCard = !WILD_CARD.equals(segment);
        if (doesNotWildCard) {
            throw new RuntimeException("first segment does not wild card.");
        }

        this.baseUrl = baseUrl;
    }

    @Override
    public List<PathUrl> consume(PathUrl requestUrl) {
        Objects.requireNonNull(requestUrl);

        PathUrl copiedBaseUrl = baseUrl.copy();
        PathUrl copiedRequestUrl = requestUrl.copy();

        copiedBaseUrl.popSegment();

        boolean onlyHasWildCard = copiedBaseUrl.isEmtpy();
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
            boolean doesNotSufficientRequestSegment = copiedBaseUrl.size() > copiedRequestUrl.size();
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
