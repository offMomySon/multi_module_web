package com.main.task.value;

import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import vo.HttpRequest;
import vo.HttpResponse;

public class HttpParameterValueMatcher<T> implements MethodParameterValueMatcher {
    private static final Set<Class<?>> HTTP_PARAMETER = Set.of(HttpRequest.class, HttpResponse.class);

    private final T httpObject;

    public HttpParameterValueMatcher(T httpObject) {
        Objects.requireNonNull(httpObject);

        Class<?> httpObjectClass = httpObject.getClass();
        boolean doesNotHttpParameter = !HTTP_PARAMETER.contains(httpObjectClass);
        if (doesNotHttpParameter) {
            throw new RuntimeException(MessageFormat.format("Does not Http Object. object class : `{}`", httpObject.getClass()));
        }

        this.httpObject = httpObject;
    }

    @Override
    public ParameterValue<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Class<?> httpObjectClass = httpObject.getClass();
        Class<?> parameterType = parameter.getType();
        if (httpObjectClass != parameterType) {
            throw new RuntimeException("does not match parameter type.");
        }

        return ParameterValue.from(httpObject);
    }
}
