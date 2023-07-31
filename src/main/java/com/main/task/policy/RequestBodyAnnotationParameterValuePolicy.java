package com.main.task.policy;

import java.io.IOException;
import java.io.InputStream;
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
    private final InputStream bodyInputStream;

    private RequestBodyAnnotationParameterValuePolicy(RequestBody requestBody, InputStream bodyInputStream) {
        Objects.requireNonNull(requestBody);
        Objects.requireNonNull(bodyInputStream);
        this.requestBody = requestBody;
        this.bodyInputStream = bodyInputStream;
    }

    public static RequestBodyAnnotationParameterValuePolicy from(Parameter parameter, Object bodyInputStream) {
        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            throw new RuntimeException("requestBody 만 받을 수 있습니다.");
        }
        Objects.requireNonNull(bodyInputStream);
        if (!(bodyInputStream instanceof InputStream)) {
            throw new RuntimeException("does not inputStream instance.");
        }

        RequestBody requestBody = optionalRequestBody.get();
        return new RequestBodyAnnotationParameterValuePolicy(requestBody, (InputStream) bodyInputStream);
    }

    public Optional<Object> getValue() {
        boolean required = requestBody.required();
        boolean isBodyEmpty = isBodyEmpty(bodyInputStream);

        boolean doesNotPossibleCreate = required && isBodyEmpty;
        if (doesNotPossibleCreate) {
            throw new RuntimeException("메세지가 비어 생성할 수 없습니다.");
        }

        boolean isPossibleEmptyBody = !required && isBodyEmpty;
        if (isPossibleEmptyBody) {
            return Optional.empty();
        }
        return Optional.of(bodyInputStream);
    }

    private static boolean isBodyEmpty(InputStream bodyInputStream) {
        try {
            return bodyInputStream.available() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
