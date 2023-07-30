package com.main.task.value;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import vo.HttpRequest;

public class HttpRequestParameterValueMatcher implements MethodParameterValueMatcher {
    private final HttpRequest httpRequest;

    public HttpRequestParameterValueMatcher(HttpRequest httpRequest) {
        Objects.requireNonNull(httpRequest);
        this.httpRequest = httpRequest;
    }

    @Override
    public Optional<Object> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Class<? extends HttpRequest> httpRequestClass = httpRequest.getClass();
        Class<?> parameterType = parameter.getType();

        if (httpRequestClass != parameterType) {
            throw new RuntimeException("does not match parameter type.");
        }

        return Optional.of(httpRequest);
    }
}
