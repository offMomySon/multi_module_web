package matcher.segment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractPathVariableSegmentChunk implements SegmentChunk {
    private final LinkedHashMap<PathUrl2, PathVariableValue> matchedPathVariables = new LinkedHashMap<>();

    @Override
    public List<PathUrl2> consume(PathUrl2 pathUrl) {
        Objects.requireNonNull(pathUrl);

        matchedPathVariables.clear();

        Map<PathUrl2, PathVariableValue> pathUrlPathVariableValueMap = internalConsume(pathUrl);

        pathUrlPathVariableValueMap.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .forEach(entry -> matchedPathVariables.putIfAbsent(entry.getKey(), entry.getValue()));

        return pathUrlPathVariableValueMap.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    public PathVariableValue find(PathUrl2 remainPathUrl) {
        Objects.requireNonNull(remainPathUrl);
        return matchedPathVariables.getOrDefault(remainPathUrl, PathVariableValue.empty());
    }

    public Map<PathUrl2, PathVariableValue> getMatchedPathVariables() {
        return new HashMap<>(matchedPathVariables);
    }

    public abstract LinkedHashMap<PathUrl2, PathVariableValue> internalConsume(PathUrl2 pathUrl);
}
