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

        Map<PathUrl, PathVariableValue> pathUrlPathVariableValueMap = internalConsume(pathUrl);

        this.matchedPathVariables = pathUrlPathVariableValueMap.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        return matchedPathVariables.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    public PathVariableValue find(PathUrl remainPathUrl) {
        Objects.requireNonNull(remainPathUrl);
        return matchedPathVariables.getOrDefault(remainPathUrl, PathVariableValue.empty());
    }

    public Map<PathUrl, PathVariableValue> getMatchedPathVariables() {
        return new HashMap<>(matchedPathVariables);
    }

    public abstract Map<PathUrl, PathVariableValue> internalConsume(PathUrl pathUrl);
}
