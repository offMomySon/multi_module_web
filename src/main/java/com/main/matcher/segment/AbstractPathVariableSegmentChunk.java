package com.main.matcher.segment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractPathVariableSegmentChunk implements SegmentChunk {
    private final LinkedHashMap<PathUrl, PathVariableValue> matchedPathVariables = new LinkedHashMap<>();

    @Override
    public List<PathUrl> consume(PathUrl pathUrl) {
        Objects.requireNonNull(pathUrl);

        matchedPathVariables.clear();

        Map<PathUrl, PathVariableValue> pathUrlPathVariableValueMap = internalConsume(pathUrl);

        pathUrlPathVariableValueMap.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .forEach(entry -> matchedPathVariables.putIfAbsent(entry.getKey(), entry.getValue()));

        return pathUrlPathVariableValueMap.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    public PathVariableValue find(PathUrl remainPathUrl) {
        Objects.requireNonNull(remainPathUrl);
        return matchedPathVariables.getOrDefault(remainPathUrl, PathVariableValue.empty());
    }

    public Map<PathUrl, PathVariableValue> getMatchedPathVariables() {
        return new HashMap<>(matchedPathVariables);
    }

    public abstract LinkedHashMap<PathUrl, PathVariableValue> internalConsume(PathUrl pathUrl);
}
