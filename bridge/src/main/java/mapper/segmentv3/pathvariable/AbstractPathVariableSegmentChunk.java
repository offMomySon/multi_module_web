package mapper.segmentv3.pathvariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.segmentv3.PathUrl;
import mapper.segmentv3.PathVariable;
import mapper.segmentv3.SegmentChunk;

public abstract class AbstractPathVariableSegmentChunk implements SegmentChunk {
    private Map<PathUrl, PathVariable> matchedPathVariables;

    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        Objects.requireNonNull(pathUrl);

        List<MatchedPathVariable> matchedPathVariables = internalConsume(pathUrl);

        this.matchedPathVariables = matchedPathVariables.stream()
            .map(matchedPathVariable -> Map.entry(matchedPathVariable.getLeftPathUrl(), matchedPathVariable.getPathVariable()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> curr));

        return matchedPathVariables.stream().map(MatchedPathVariable::getLeftPathUrl).collect(Collectors.toUnmodifiableList());
    }

    public Map<PathUrl, PathVariable> getMatchedPathVariables() {
        return new HashMap<>(matchedPathVariables);
    }

    public abstract List<MatchedPathVariable> internalConsume(PathUrl pathUrl);
}
