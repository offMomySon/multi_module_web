package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParameterValueMatchers {
    private final Map<ValueMatcherType, ParameterValueMatcher> matchers;

    public ParameterValueMatchers(Map<ValueMatcherType, ParameterValueMatcher> matchers) {
        Objects.requireNonNull(matchers);
        this.matchers = matchers.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));;
    }

    public Optional<?> match(ParameterAndValueMatcherType parameterAndValueMatcherType) {
        Objects.requireNonNull(parameterAndValueMatcherType);

        ValueMatcherType valueMatcherType = parameterAndValueMatcherType.getValueMatcherType();
        if(!matchers.containsKey(valueMatcherType)){
            throw new RuntimeException("Does not exist match type.");
        }
        ParameterValueMatcher parameterValueMatcher = matchers.get(valueMatcherType);

        Parameter parameter = parameterAndValueMatcherType.getParameter();
        Optional optionalMatched = parameterValueMatcher.match(parameter);
        log.info("matched value : `{}`", optionalMatched);

        return optionalMatched;
    }
}
