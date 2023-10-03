package parameter.matcher;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CompositeMethodParameterValueMatcher implements MethodParameterValueMatcher {
    private final ParameterTypeFinder parameterTypeFinder;
    private final Map<ParameterType, MethodParameterValueMatcher> matchers;

    public CompositeMethodParameterValueMatcher(ParameterTypeFinder parameterTypeFinder, Map<ParameterType, MethodParameterValueMatcher> matchers) {
        Objects.requireNonNull(parameterTypeFinder);
        Objects.requireNonNull(matchers);
        this.parameterTypeFinder = parameterTypeFinder;
        this.matchers = matchers.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));;
    }

    @Override
    public Optional<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        ParameterType parameterType = parameterTypeFinder.find(parameter);
        MethodParameterValueMatcher methodParameterValueMatcher = matchers.get(parameterType);

        return methodParameterValueMatcher.match(parameter);
    }
}
