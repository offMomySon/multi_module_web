package matcher.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompositeParameterConverter implements ParameterConverter {
    private final Map<Class<? extends Annotation>, ParameterConverter> parameterConverters;

    public CompositeParameterConverter(Map<Class<? extends Annotation>, ParameterConverter> parameterConverters) {
        if (Objects.isNull(parameterConverters)) {
            throw new RuntimeException("parameterConverters is null.");
        }

        Map<Class<? extends Annotation>, ParameterConverter> newParameterConverters = parameterConverters.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));

        if (newParameterConverters.isEmpty()) {
            throw new RuntimeException("newParameterConverters is empty.");
        }

        this.parameterConverters = newParameterConverters;
    }

    @Override
    public Optional<Object> convertAsValue(Parameter parameter) {
        Objects.requireNonNull(parameter);

        ParameterConverter foundConverter = findParameterConverter(parameter);
        log.info("foundConverter : {}", foundConverter);

        return foundConverter.convertAsValue(parameter);
    }

    private ParameterConverter findParameterConverter(Parameter parameter) {
        List<? extends Class<? extends Annotation>> annotationTypes = Arrays.stream(parameter.getDeclaredAnnotations())
            .map(Annotation::annotationType)
            .collect(Collectors.toUnmodifiableList());

        return annotationTypes.stream()
            .peek(annotationType -> log.info("annotationType : {}", annotationType))
            .filter(parameterConverters::containsKey)
            .map(parameterConverters::get)
            .findAny()
            .orElseThrow(() -> new RuntimeException("does not exist converter"));
    }
}
