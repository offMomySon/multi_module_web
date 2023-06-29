package com.main.extractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestParam;
import matcher.converter.RequestParameters;
import util.AnnotationUtils;

public class BaseParameterValueExtractor implements ParameterValueExtractor {
    private final Class<?> targetAnnotationClazz;
    private final RequestParameters requestParameters;
    private final Parameter parameter;

    public BaseParameterValueExtractor(Class<?> targetAnnotationClazz, RequestParameters requestParameters, Parameter parameter) {
        Objects.requireNonNull(targetAnnotationClazz);
        Objects.requireNonNull(requestParameters);
        Objects.requireNonNull(parameter);
        this.targetAnnotationClazz = targetAnnotationClazz;
        this.requestParameters = requestParameters;
        this.parameter = parameter;
    }

    @Override
    public ExtractValue extract() {
        Optional<?> optionalTargetAnnotation = AnnotationUtils.find(parameter, targetAnnotationClazz);

        boolean doesNotTargetAnnotation = optionalTargetAnnotation.isEmpty();
        if (doesNotTargetAnnotation) {
            return new ExtractValue(parameter.getType(), Optional.empty());
        }

        AnnotationValue annotationValue = AnnotationValue.from((Annotation) optionalTargetAnnotation.get());

        String bindName = parameter.getName();
        boolean existAnnotationName = !annotationValue.getName().isBlank();
        if (existAnnotationName) {
            bindName = annotationValue.getName();
        }
        String defaultValue = annotationValue.getDefaultValue().orElse(null);
        String foundValueOrNull = requestParameters.getOrDefault(bindName, defaultValue);

        boolean doesNotExistValue = Objects.isNull(foundValueOrNull) && annotationValue.isRequired();
        if (doesNotExistValue) {
            throw new RuntimeException("does not exist value.");
        }

        if (Objects.isNull(foundValueOrNull)) {
            return new ExtractValue(parameter.getType(), Optional.empty());
        }

        return new ExtractValue(parameter.getType(), Optional.ofNullable(foundValueOrNull));
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
