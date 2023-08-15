package com.main.task.value;

import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestParam;
import matcher.converter.RequestParameters;

/*
 *  todo
 *   HttpUrlAnnotationAnnotatedParameterValueMatcher 네이밍의 판단 근거.
 *   requestParam, pathVariable 각각의 개념이 존재하지만,
 *   근본적으로 http url 로 부터 parameter 에 값을 할당한다.
 *   그렇기 때문에 requestParam, pathVairalbe 을 아우르기 위해서 http url annotation 이란 키워드를 바탕으로 네이밍을 하였다.
 */
public class HttpUrlAnnotationAnnotatedParameterValueMatcher2<T> implements MethodParameterValueMatcher {
    private static final String EMPTY_VALUE = null;
    private static final Set<Class<?>> HTTP_URL_ANNOTATION_CLASSES = Set.of(RequestParam.class, PathVariable.class);

    private final Class<T> paramAnnotationClazz;
    private final RequestParameters requestParameters;

    public HttpUrlAnnotationAnnotatedParameterValueMatcher2(Class<T> parameterAnnotationClazz, RequestParameters requestParameters) {
        Objects.requireNonNull(parameterAnnotationClazz);
        Objects.requireNonNull(requestParameters);

        boolean doesNotBaseParameterAnnotation = !HTTP_URL_ANNOTATION_CLASSES.contains(parameterAnnotationClazz);
        if (doesNotBaseParameterAnnotation) {
            throw new RuntimeException(MessageFormat.format("does not base annotation. parameterAnnotationClazz : `{}`", parameterAnnotationClazz));
        }

        this.paramAnnotationClazz = parameterAnnotationClazz;
        this.requestParameters = requestParameters;
    }

    @Override
    public Optional<Object> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<T> optionalParameterAnnotation = AnnotationUtils.find(parameter, paramAnnotationClazz);
        if (optionalParameterAnnotation.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("does not exist annotation. parameter : `{}`, paramAnnotationClazz : `{}`", parameter, paramAnnotationClazz));
        }

        Annotation annotation = (Annotation) optionalParameterAnnotation.get();
        HttpUrlAnnotationParameterName httpUrlAnnotationParameterName = HttpUrlAnnotationParameterName.from(annotation);
        String bindName = !httpUrlAnnotationParameterName.isParameterNameBlank() ?
            httpUrlAnnotationParameterName.getValue() :
            parameter.getName();

        return Optional.ofNullable(requestParameters.getOrDefault(bindName, EMPTY_VALUE));
    }

    public static class HttpUrlAnnotationParameterName {
        private final String value;

        public HttpUrlAnnotationParameterName(String value) {
            Objects.requireNonNull(value);
            this.value = value;
        }

        public static HttpUrlAnnotationParameterName from(Annotation annotation) {
            Objects.requireNonNull(annotation);

            if (annotation instanceof RequestParam) {
                RequestParam requestParam = (RequestParam) annotation;
                String parameterName = requestParam.value();
                return new HttpUrlAnnotationParameterName(parameterName);
            }

            if (annotation instanceof PathVariable) {
                PathVariable pathVariable = (PathVariable) annotation;
                String parameterName = pathVariable.value();
                return new HttpUrlAnnotationParameterName(parameterName);
            }

            throw new RuntimeException(MessageFormat.format("does not possible create ParameterName whit this Annotation. Annotation : `{}`", annotation));
        }

        public boolean isParameterNameBlank() {
            return value.isBlank();
        }

        public String getValue() {
            return value;
        }
    }
}
