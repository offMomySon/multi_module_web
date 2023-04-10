package variableExtractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import marker.PathVariable;
import marker.RequestParam;
import util.AnnotationUtils;
import vo.ParamAnnotationValue;
import vo.RequestValues;

public class RequestParameterConverter implements ParameterConverter {
    private static final Class<RequestParam> REQUEST_PARAM_CLASS = RequestParam.class;
    private static final Class<PathVariable> PATH_VARIABLE_CLASS = PathVariable.class;
    private static final String EMPTY_DEFAULT_VALUE = null;

    private final RequestValues requestValues;
    private final Class<?> targetAnnotationType;

    public RequestParameterConverter(Class<?> targetAnnotationType, RequestValues requestValues) {
        Objects.requireNonNull(requestValues, "requestParameters is null");
        Objects.requireNonNull(targetAnnotationType, "targetAnnotation is null");

        if (REQUEST_PARAM_CLASS != targetAnnotationType && PATH_VARIABLE_CLASS != targetAnnotationType) {
            throw new IllegalArgumentException("does not convertable target.");
        }

        this.targetAnnotationType = targetAnnotationType;
        this.requestValues = requestValues;
    }

    public static RequestParameterConverter from(Annotation annotation, RequestValues requestValues) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(requestValues);

        Class<? extends Annotation> annotationType = annotation.annotationType();

        boolean doesNotPossibleConvertAnnotation = REQUEST_PARAM_CLASS != annotationType && PATH_VARIABLE_CLASS != annotationType;
        if (doesNotPossibleConvertAnnotation) {
            throw new RuntimeException("does Not Possible Convert Annotation");
        }

        return new RequestParameterConverter(annotationType, requestValues);
    }

    public Optional<Object> convertAsValue(Parameter parameter) {
        Objects.requireNonNull(parameter, "parameter is null");

        Optional<RequestParam> optionalRequestParam = AnnotationUtils.find(parameter, REQUEST_PARAM_CLASS);
        Optional<PathVariable> optionalPathVariable = AnnotationUtils.find(parameter, PATH_VARIABLE_CLASS);

        boolean doesNotConvertableParameter = optionalRequestParam.isEmpty() && optionalPathVariable.isEmpty();
        if (doesNotConvertableParameter) {
            return Optional.empty();
        }

        ParamAnnotationValue paramAnnotationValue = optionalRequestParam.isEmpty() ? ParamAnnotationValue.from(optionalPathVariable.get()) : ParamAnnotationValue.from(optionalRequestParam.get());

        boolean doesNotTargetAnnotationType = paramAnnotationValue.getAnnotationType() != targetAnnotationType;
        if (doesNotTargetAnnotationType) {
            return Optional.empty();
        }

        String findParamName = paramAnnotationValue.getName().isBlank() || paramAnnotationValue.getName().isEmpty() ? parameter.getName() : paramAnnotationValue.getName();
        String defaultValueOrNull = paramAnnotationValue.getDefaultValue().orElse(EMPTY_DEFAULT_VALUE);
        String paramValueOrNull = requestValues.getOrDefault(findParamName, defaultValueOrNull);

        boolean doesNotAbleToCreate = Objects.isNull(paramValueOrNull) && paramAnnotationValue.isRequired();
        if (doesNotAbleToCreate) {
            throw new RuntimeException("path value does not exist.");
        }

        return Optional.ofNullable(paramValueOrNull);
    }
}
