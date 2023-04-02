package variableExtractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import mapper.AnnotationUtils;
import marker.PathVariable;
import marker.RequestParam;
import vo.ParamAnnotation;
import vo.RequestParameters;

public class RequestParameterConverter implements ParameterConverter {
    private static final Class<RequestParam> REQUEST_PARAM_CLASS = RequestParam.class;
    private static final Class<PathVariable> PATH_VARIABLE_CLASS = PathVariable.class;
    private static final String DEFAULT_VALUE = null;

    private final RequestParameters requestParameters;

    public RequestParameterConverter(RequestParameters requestParameters) {
        if (Objects.isNull(requestParameters)) {
            throw new RuntimeException("requestParameters is null.");
        }
        this.requestParameters = requestParameters;
    }

    public Optional<Object> convertValue(Parameter parameter) {
        Optional<RequestParam> optionalRequestParam = AnnotationUtils.find(parameter, REQUEST_PARAM_CLASS);
        Optional<PathVariable> optionalPathVariable = AnnotationUtils.find(parameter, PATH_VARIABLE_CLASS);

        boolean doesNotHaveAnyAnnotation = optionalRequestParam.isEmpty() && optionalPathVariable.isEmpty();
        if (doesNotHaveAnyAnnotation) {
            throw new RuntimeException("does not have any annotation.");
        }

        ParamAnnotation paramAnnotation = optionalRequestParam.isEmpty() ?
            ParamAnnotation.from(optionalPathVariable.get()) : ParamAnnotation.from(optionalRequestParam.get());

        boolean isAnnotationParamNameEmpty = paramAnnotation.getName().isEmpty() || paramAnnotation.getName().isBlank();
        String findName = isAnnotationParamNameEmpty ? parameter.getName() : paramAnnotation.getName();

        String defaultValue = paramAnnotation.getDefaultValue().isPresent() ? paramAnnotation.getDefaultValue().get() : DEFAULT_VALUE;
        String valueOrNull = requestParameters.getOrDefault(findName, defaultValue);

        boolean doesNotPossibleCreate = Objects.isNull(valueOrNull) && paramAnnotation.isRequired();
        if (doesNotPossibleCreate) {
            throw new RuntimeException("path value does not exist.");
        }

        return Optional.ofNullable(valueOrNull);
    }

}
