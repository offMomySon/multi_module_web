package com.main.task.policy;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.annotation.RequestBody;
import util.AnnotationUtils;

@Slf4j
public class RequestBodyAnnotationParameterValuePolicy implements ParameterValuePolicy {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;

    private final RequestBody requestBody;
    private final String body;

    private RequestBodyAnnotationParameterValuePolicy(RequestBody requestBody, String body) {
        Objects.requireNonNull(requestBody);
        Objects.requireNonNull(body);
        this.requestBody = requestBody;
        this.body = body;
    }

    public static RequestBodyAnnotationParameterValuePolicy from(Parameter parameter, String body) {
        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            throw new RuntimeException("requestBody 만 받을 수 있습니다.");
        }
        Objects.requireNonNull(body);

        RequestBody requestBody = optionalRequestBody.get();
        return new RequestBodyAnnotationParameterValuePolicy(requestBody, body);
    }

    public Optional<Object> getValue() {
        boolean required = requestBody.required();
        boolean isBodyEmpty = body.isEmpty();

        boolean doesNotPossibleCreate = required && isBodyEmpty;
        if (doesNotPossibleCreate) {
            throw new RuntimeException("메세지가 비어 생성할 수 없습니다.");
        }

        boolean isPossibleEmptyBody = !required && isBodyEmpty;
        if (isPossibleEmptyBody) {
            return Optional.empty();
        }
        return Optional.of(body);
    }
}
