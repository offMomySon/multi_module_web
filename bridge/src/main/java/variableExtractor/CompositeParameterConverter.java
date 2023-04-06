package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompositeParameterConverter implements ParameterConverter {
    private final List<ParameterConverter> parameterConverters;

    public CompositeParameterConverter(List<ParameterConverter> parameterConverters) {
        if (Objects.isNull(parameterConverters)) {
            throw new RuntimeException("parameterConverters is null.");
        }

        List<ParameterConverter> newJavaMethodResolver = parameterConverters.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newJavaMethodResolver.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.parameterConverters = newJavaMethodResolver;
    }

    @Override
    public Optional<Object> convertAsValue(Parameter parameter) {
        if (Objects.isNull(parameter)) {
            return Optional.empty();
        }
        
        return parameterConverters.stream()
            .map(converter -> converter.convertAsValue(parameter))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }
}
