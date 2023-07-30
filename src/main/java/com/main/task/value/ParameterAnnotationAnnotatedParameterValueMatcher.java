package com.main.task.value;

import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestParam;
import matcher.converter.RequestParameters;

public class ParameterAnnotationAnnotatedParameterValueMatcher<T> implements MethodParameterValueMatcher {
    private static final String EMPTY_VALUE = null;

    private final Class<T> paramAnnotationClazz;
    private final RequestParameters requestParameters;

    public ParameterAnnotationAnnotatedParameterValueMatcher(Class<T> parameterAnnotationClazz, RequestParameters requestParameters) {
        Objects.requireNonNull(parameterAnnotationClazz);
        Objects.requireNonNull(requestParameters);
        paramAnnotationClazz = parameterAnnotationClazz;
        this.requestParameters = requestParameters;
    }

    @Override
    public Optional<Object> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<T> optionalPathVariable = AnnotationUtils.find(parameter, paramAnnotationClazz);
        if (optionalPathVariable.isEmpty()) {
            throw new RuntimeException("does not exist PathVariable annotation.");
        }

        Annotation annotation = (Annotation) optionalPathVariable.get();
        ParameterNameAttribute parameterNameAttribute = ParameterNameAttribute.from(annotation);

        String bindName = !parameterNameAttribute.isBlank() ?
            parameterNameAttribute.getValue() :
            parameter.getName();

        String matchValue = requestParameters.getOrDefault(bindName, EMPTY_VALUE);
        return Optional.ofNullable(matchValue);
    }

    public static class ParameterNameAttribute {
        private final String value;

        public ParameterNameAttribute(String value) {
            Objects.requireNonNull(value);
            this.value = value;
        }

        public static ParameterNameAttribute from(Annotation annotation) {
            Objects.requireNonNull(annotation);

            if (annotation instanceof RequestParam) {
                RequestParam requestParam = (RequestParam) annotation;
                String value = requestParam.value();
                return new ParameterNameAttribute(value);
            }

            if (annotation instanceof PathVariable) {
                PathVariable pathVariable = (PathVariable) annotation;
                String value = pathVariable.value();
                return new ParameterNameAttribute(value);
            }

            throw new RuntimeException(MessageFormat.format("does not possible create ParameterName whit this Annotation. Annotation : `{}`", annotation));
        }

        public boolean isBlank() {
            return value.isBlank();
        }

        public String getValue() {
            return value;
        }
    }
}
