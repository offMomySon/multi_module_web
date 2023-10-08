package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParameterValueMatchers {
    private final Map<ParameterValueAssigneType, ParameterValueMatcher> matchers;

    public ParameterValueMatchers(Map<ParameterValueAssigneType, ParameterValueMatcher> matchers) {
        Objects.requireNonNull(matchers);
        this.matchers = matchers.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));;
    }

    public Optional<?> match(ParameterAndValueAssigneeType parameterAndValueAssigneeType) {
        Objects.requireNonNull(parameterAndValueAssigneeType);

        ParameterValueAssigneType parameterValueAssigneType = parameterAndValueAssigneeType.getParameterValueAssigneType();
        if(!matchers.containsKey(parameterValueAssigneType)){
            throw new RuntimeException("Does not exist match type.");
        }
        ParameterValueMatcher parameterValueMatcher = matchers.get(parameterValueAssigneType);

        Parameter parameter = parameterAndValueAssigneeType.getParameter();
        Optional optionalMatched = parameterValueMatcher.match(parameter);
        log.info("matched value : `{}`", optionalMatched);

        return optionalMatched;
    }
}
