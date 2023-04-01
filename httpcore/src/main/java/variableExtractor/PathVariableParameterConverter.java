package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.PathVariable;

public class PathVariableParameterConverter implements ParameterConverter {
    private static final Class<PathVariable> PATH_VARIABLE_CLASS = PathVariable.class;

    private final Map<String, String> pathValues;

    public PathVariableParameterConverter(Map<String, String> pathValues) {
        this.pathValues = pathValues;
    }

    public Optional<Object> convertValue(Parameter parameter) {
        Optional<PathVariable> optionalPathVariable = AnnotationUtils.find(parameter, PATH_VARIABLE_CLASS);
        if (optionalPathVariable.isEmpty()) {
            return Optional.empty();
        }

        PathVariable pathVariable = optionalPathVariable.get();

        String findName = Objects.isNull(pathVariable.value()) ? parameter.getName() : pathVariable.value();

        Object objectOrNull = pathValues.get(findName);

        if (Objects.isNull(objectOrNull) && pathVariable.required()) {
            throw new RuntimeException("path value does not exist.");
        }

        return Optional.ofNullable(objectOrNull);
    }
}
