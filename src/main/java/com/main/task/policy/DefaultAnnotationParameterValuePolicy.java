package com.main.task.policy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestParam;
import util.AnnotationUtils;

public class DefaultAnnotationParameterValuePolicy implements ParameterValuePolicy {
    private static final String DEFAULT_VALUE = null;

    private final AnnotationValue annotationValue;
    private final Optional<String> parameterValue;

    private DefaultAnnotationParameterValuePolicy(AnnotationValue annotationValue, String parameterValue) {
        Objects.requireNonNull(annotationValue);
        this.annotationValue = annotationValue;
        this.parameterValue = Optional.of(parameterValue);
    }

    public static DefaultAnnotationParameterValuePolicy from(Parameter parameter, Class<?> targetAnnotationClazz, String parameterValue) {
        Optional<?> optionalTargetAnnotation = AnnotationUtils.find(parameter, targetAnnotationClazz);
        boolean doesNotTargetAnnotation = optionalTargetAnnotation.isEmpty();
        if (doesNotTargetAnnotation) {
            throw new RuntimeException("does not match target annotation.");
        }

        AnnotationValue annotationValue = AnnotationValue.from((Annotation) optionalTargetAnnotation.get());
        return new DefaultAnnotationParameterValuePolicy(annotationValue, parameterValue);
    }

    public Optional<Object> getValue() {
        String defaultValueOrNull = annotationValue.getDefaultValue().orElse(DEFAULT_VALUE);
        String parameterValueOrNull = parameterValue.orElse(defaultValueOrNull);
        boolean isValueRequire = annotationValue.isRequired();

        boolean doesNotPossibleConvert = Objects.isNull(parameterValueOrNull) && isValueRequire;
        if (doesNotPossibleConvert) {
            throw new RuntimeException("does not possible convert.");
        }

        if (Objects.isNull(parameterValueOrNull)) {
            return Optional.empty();
        }
        return Optional.of(parameterValueOrNull);
    }

    private static class AnnotationValue {
        private final Optional<String> defaultValue;
        private final boolean required;

        public AnnotationValue(String defaultValue, boolean required) {
            this.defaultValue = Optional.ofNullable(defaultValue);
            this.required = required;
        }

        public static AnnotationValue from(Annotation annotation) {
            if (annotation instanceof RequestParam) {
                RequestParam requestParam = (RequestParam) annotation;
                String defaultValue = requestParam.defaultValue().isBlank() ? null : requestParam.defaultValue();
                return new AnnotationValue(defaultValue, requestParam.required());
            }

            if (annotation instanceof PathVariable) {
                PathVariable pathVariable = (PathVariable) annotation;
                return new AnnotationValue(null, pathVariable.required());
            }

            throw new RuntimeException("does not possible to create.");
        }

        public Optional<String> getDefaultValue() {
            return defaultValue;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
