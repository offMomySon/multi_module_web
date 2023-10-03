package parameter.matcher;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CompositeMethodParameterValueMatcher implements MethodParameterValueMatcher {
    private final Map<ParameterType, MethodParameterValueMatcher> matchers;

    public CompositeMethodParameterValueMatcher(Map<ParameterType, MethodParameterValueMatcher> matchers) {
        Objects.requireNonNull(matchers);

        matchers = matchers.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
        this.matchers = matchers;
    }

    @Override
    public Optional<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<Class<?>> optionalMatchedAnnotationType = findAnnotatedClassForParameter(parameter, matchers.keySet());
        boolean existMatchedAnnotationType = optionalMatchedAnnotationType.isPresent();
        if (existMatchedAnnotationType) {
            Class<?> annotationType = optionalMatchedAnnotationType.get();
            MethodParameterValueMatcher methodParameterValueMatcher = matchers.get(annotationType);
            return methodParameterValueMatcher.match(parameter);
        }

        Optional<Class<?>> optionalFoundParameterType = findMatchParameterType(parameter.getType(), matchers.keySet());
        boolean foundParameterType = optionalFoundParameterType.isPresent();
        if (foundParameterType) {
            Class<?> matchedParameterType = optionalFoundParameterType.get();
            MethodParameterValueMatcher methodParameterValueMatcher = matchers.get(matchedParameterType);
            return methodParameterValueMatcher.match(parameter);
        }

        return Optional.empty();
    }

    private static Optional<Class<?>> findAnnotatedClassForParameter(Parameter parameter, Set<Class<?>> findAnnotationClasses) {
        return findAnnotationClasses.stream()
            .filter(type -> AnnotationUtils.exist(parameter, type))
            .findFirst();
    }

    private static Optional<Class<?>> findMatchParameterType(Class<?> parameterType, Set<Class<?>> findParameterTypes) {
        return findParameterTypes.stream()
            .filter(type -> type == parameterType)
            .findFirst();
    }
}
