package com.main.task.value;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import vo.HttpResponse;

public class HttpResponseParameterValueMatcher implements MethodParameterValueMatcher {
    private final HttpResponse httpResponse;

    public HttpResponseParameterValueMatcher(HttpResponse httpResponse) {
        Objects.requireNonNull(httpResponse);
        this.httpResponse = httpResponse;
    }

    @Override
    public Optional<Object> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Class<? extends HttpResponse> httpResponseClass = httpResponse.getClass();
        Class<?> parameterType = parameter.getType();

        if (httpResponseClass != parameterType) {
            throw new RuntimeException("does not match parameter type.");
        }

        return Optional.of(httpResponse);
    }
}

