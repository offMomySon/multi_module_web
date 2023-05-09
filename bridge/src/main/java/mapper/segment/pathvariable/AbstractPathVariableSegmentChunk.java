package mapper.segment.pathvariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import mapper.segment.PathUrl;
import mapper.segment.PathVariableValue;
import mapper.segment.SegmentChunk;

public abstract class AbstractPathVariableSegmentChunk implements SegmentChunk {
    private Map<PathUrl, PathVariableValue> matchedPathVariables;

    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        Objects.requireNonNull(pathUrl);

        List<MatchedPathVariable> matchedPathVariables = internalConsume(pathUrl);

        this.matchedPathVariables = matchedPathVariables.stream()
            .map(matchedPathVariable -> Map.entry(matchedPathVariable.getLeftPathUrl(), matchedPathVariable.getPathVariable()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> curr));

        return matchedPathVariables.stream().map(MatchedPathVariable::getLeftPathUrl).collect(Collectors.toUnmodifiableList());
    }

    public Map<PathUrl, PathVariableValue> getMatchedPathVariables() {
        return new HashMap<>(matchedPathVariables);
    }

    public abstract List<MatchedPathVariable> internalConsume(PathUrl pathUrl);
}
