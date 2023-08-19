package com.main.task.policy;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.annotation.RequestBody;

@Slf4j
public class HttpBodyAnnotationAnnotatedParameterRequireChecker implements ParameterRequireChecker {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;

    @Override
    public void check(Optional<Object> parameterValue) {
    }
}
