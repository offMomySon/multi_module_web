package variableExtractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import marker.PathVariable;
import marker.RequestParam;
import util.AnnotationUtils;
import vo.ParameterValues;

public class V2RequestParameterConverter {
    private final ParameterValues parameterValues;
    private final Class<?> annotationType;

    public V2RequestParameterConverter(ParameterValues parameterValues, Class<?> annotationType) {
        this.parameterValues = parameterValues;
        this.annotationType = annotationType;
    }


    public Optional<Object> convertAsValue(Parameter parameter) {
        Optional<?> paramAnnotaion = AnnotationUtils.find(parameter, annotationType);
        if (paramAnnotaion.isEmpty()) {
            return Optional.empty();
        }

        Annotation annotation = (Annotation) paramAnnotaion.get();
        AnnotationValues annotationValues = AnnotationValues.from(annotation);

        String bindName = annotationValues.name;
        if (bindName.isEmpty() || bindName.isBlank()) {
            bindName = parameter.getName();
        }
        String foundValueOrNull = parameterValues.getOrDefault(bindName, annotationValues.defaultValue);
        boolean emptyValue = Objects.isNull(foundValueOrNull) || foundValueOrNull.isBlank();
        if (annotationValues.required && emptyValue) {
            throw new RuntimeException("does not able to convert");
        }

        return Optional.ofNullable(foundValueOrNull);
    }

    public static class AnnotationValues {
        private final String name;
        private final String defaultValue;
        private final boolean required;

        public AnnotationValues(String name, String defaultValue, boolean required) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.required = required;
        }

        public static AnnotationValues from(Annotation annotation) {
            if (annotation instanceof RequestParam) {
                return from((RequestParam) annotation);
            }

            if (annotation instanceof PathVariable) {
                return from((PathVariable) annotation);
            }

            throw new RuntimeException("");
        }

        public static AnnotationValues from(RequestParam requestParam) {
            return new AnnotationValues(requestParam.value(), requestParam.defaultValue(), requestParam.required());
        }

        public static AnnotationValues from(PathVariable pathVariable) {
            return new AnnotationValues(pathVariable.value(), null, pathVariable.required());
        }
    }
}
