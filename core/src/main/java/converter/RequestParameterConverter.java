package converter;

import annotation.PathVariable;
import annotation.RequestParam;
import converter.base.ObjectConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import method.segment.PathVariableValue;
import util.AnnotationUtils;
import vo.RequestParameters;

@Slf4j
public class RequestParameterConverter implements ParameterConverter {
    private static final ObjectConverter objectConverter = new ObjectConverter();

    private final Class<?> targetAnnotationClazz;
    private final RequestParameters requestParameters;

    public RequestParameterConverter(Class<?> targetAnnotationClazz, RequestParameters requestParameters) {
        Objects.requireNonNull(targetAnnotationClazz);
        Objects.requireNonNull(requestParameters);

        this.targetAnnotationClazz = targetAnnotationClazz;
        this.requestParameters = requestParameters;
    }

    public static RequestParameterConverter from(Class<?> targetAnnotationClazz, PathVariableValue pathVariableValue) {
        Objects.requireNonNull(targetAnnotationClazz);
        Objects.requireNonNull(pathVariableValue);

        return new RequestParameterConverter(targetAnnotationClazz, new RequestParameters(pathVariableValue.getValues()));
    }

    @Override
    public Optional<Object> convertAsValue(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<?> optionalTargetAnnotation = AnnotationUtils.find(parameter, targetAnnotationClazz);

        boolean doesNotTargetAnnotation = optionalTargetAnnotation.isEmpty();
        if (doesNotTargetAnnotation) {
            return Optional.empty();
        }

        AnnotationValue annotationValue = AnnotationValue.from((Annotation) optionalTargetAnnotation.get());

        String bindName = parameter.getName();
        boolean existAnnotationName = !annotationValue.getName().isBlank();
        if (existAnnotationName) {
            bindName = annotationValue.getName();
        }

        String defaultValue = annotationValue.getDefaultValue().orElse(null);
        String foundValueOrNull = requestParameters.getOrDefault(bindName, defaultValue);

        boolean doesNotPossibleConvert = Objects.isNull(foundValueOrNull) && annotationValue.isRequired();
        if (doesNotPossibleConvert) {
            throw new RuntimeException("does not possible convert.");
        }

        if (Objects.isNull(foundValueOrNull)) {
            return Optional.empty();
        }

        boolean doesNotNecessaryTypeConvert = Objects.equals(foundValueOrNull.getClass(), parameter.getType());
        if (doesNotNecessaryTypeConvert) {
            return Optional.of(foundValueOrNull);
        }

        Object convertedValue = objectConverter.convert(foundValueOrNull, parameter.getType());
        return Optional.of(convertedValue);
    }


    private static class AnnotationValue {
        private final String name;
        private final Optional<String> defaultValue;
        private final boolean required;

        public AnnotationValue(String name, String defaultValue, boolean required) {
            Objects.requireNonNull(name);

            this.name = name;
            this.defaultValue = Optional.ofNullable(defaultValue);
            this.required = required;
        }

        public static AnnotationValue from(Annotation annotation) {
            if (annotation instanceof RequestParam) {
                RequestParam requestParam = (RequestParam) annotation;

                String defaultValue = requestParam.defaultValue().isBlank() ? null : requestParam.defaultValue();

                return new AnnotationValue(requestParam.value(), defaultValue, requestParam.required());
            }

            if (annotation instanceof PathVariable) {
                PathVariable pathVariable = (PathVariable) annotation;
                return new AnnotationValue(pathVariable.value(), null, pathVariable.required());
            }

            throw new RuntimeException("does not possible to create.");
        }


        public String getName() {
            return name;
        }

        public Optional<String> getDefaultValue() {
            return defaultValue;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
