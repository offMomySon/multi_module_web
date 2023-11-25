package matcher.segment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import matcher.path.PathUrl;
import matcher.path.PathVariableValue;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class AbstractPathVariableSegmentChunk implements SegmentChunk {
    private final LinkedHashMap<PathUrl, PathVariableValue> matchedPathVariables = new LinkedHashMap<>();

    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        if (isNull(pathUrl)) {
            throw new RuntimeException("Ensure the parameter is not null.");
        }

        matchedPathVariables.clear();

        Map<PathUrl, PathVariableValue> pathUrlPathVariableValueMap = internalConsume(pathUrl);

        pathUrlPathVariableValueMap.entrySet().stream()
            .filter(entry -> nonNull(entry.getKey()))
            .filter(entry -> nonNull(entry.getValue()))
            .forEach(entry -> matchedPathVariables.putIfAbsent(entry.getKey(), entry.getValue()));

        return pathUrlPathVariableValueMap.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    public Map<PathUrl, PathVariableValue> getMatchedPathVariables() {
        return new HashMap<>(matchedPathVariables);
    }

    public abstract LinkedHashMap<PathUrl, PathVariableValue> internalConsume(PathUrl pathUrl);
}