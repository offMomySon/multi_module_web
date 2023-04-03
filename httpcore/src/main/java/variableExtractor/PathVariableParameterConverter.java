package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.PathVariable;
import vo.RequestParameters;

public class PathVariableParameterConverter implements ParameterConverter {
    private static final Class<PathVariable> PATH_VARIABLE_CLASS = PathVariable.class;

    private final RequestParameters requestParameters;

    public PathVariableParameterConverter(RequestParameters requestParameters) {
        if (Objects.isNull(requestParameters)) {
            throw new RuntimeException("requestParameters is null.");
        }
        this.requestParameters = requestParameters;
    }

    public Optional<Object> convertAsValue(Parameter parameter) {
        Optional<PathVariable> optionalPathVariable = AnnotationUtils.find(parameter, PATH_VARIABLE_CLASS);
        if (optionalPathVariable.isEmpty()) {
            return Optional.empty();
        }

        PathVariable pathVariable = optionalPathVariable.get();

        String findName = Objects.isNull(pathVariable.value()) ? parameter.getName() : pathVariable.value();

        Object objectOrNull = requestParameters.get(findName);

        boolean doesNotPossibleCreate = Objects.isNull(objectOrNull) && pathVariable.required();
        if (doesNotPossibleCreate) {
            throw new RuntimeException("path value does not exist.");
        }

        return Optional.ofNullable(objectOrNull);
    }
}
