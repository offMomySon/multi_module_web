package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.PathVariable;
import marker.RequestParam;
import vo.ParamAnnotationValue;
import vo.RequestParameters;

public class RequestParameterConverter implements ParameterConverter {
    private static final Class<RequestParam> REQUEST_PARAM_CLASS = RequestParam.class;
    private static final Class<PathVariable> PATH_VARIABLE_CLASS = PathVariable.class;
    private static final String EMPTY_DEFAULT_VALUE = null;

    private final RequestParameters requestParameters;
    private final ParamAnnotationValue annotationValue;

    public RequestParameterConverter(RequestParameters requestParameters, ParamAnnotationValue annotationValue) {
        Objects.requireNonNull(requestParameters, "requestParameters is null");
        Objects.requireNonNull(annotationValue, "annotationValue is null");

        this.requestParameters = requestParameters;
        this.annotationValue = annotationValue;
    }

    public Optional<Object> convertAsValue(Parameter parameter) {
        Objects.requireNonNull(parameter, "parameter is null");

        Optional<RequestParam> optionalRequestParam = AnnotationUtils.find(parameter, REQUEST_PARAM_CLASS);
        Optional<PathVariable> optionalPathVariable = AnnotationUtils.find(parameter, PATH_VARIABLE_CLASS);

        boolean doesNotConvertableParameter = optionalRequestParam.isEmpty() && optionalPathVariable.isEmpty();
        if (doesNotConvertableParameter) {
            throw new IllegalArgumentException("does not convertable parameter.");
        }

        String findParamName = annotationValue.getName().isBlank() || annotationValue.getName().isEmpty() ? parameter.getName() : annotationValue.getName();
        String defaultValueOrNull = annotationValue.getDefaultValue().orElse(EMPTY_DEFAULT_VALUE);
        String paramValueOrNull = requestParameters.getOrDefault(findParamName, defaultValueOrNull);

        boolean doesNotAbleToCreate = Objects.isNull(paramValueOrNull) && annotationValue.isRequired();
        if (doesNotAbleToCreate) {
            throw new RuntimeException("path value does not exist.");
        }

        return Optional.ofNullable(paramValueOrNull);
    }
}
