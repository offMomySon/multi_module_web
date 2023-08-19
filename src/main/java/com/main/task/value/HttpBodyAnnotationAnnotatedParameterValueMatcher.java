package com.main.task.value;

import com.main.util.AnnotationUtils;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.RequestBody;

public class HttpBodyAnnotationAnnotatedParameterValueMatcher implements MethodParameterValueMatcher {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;

    private final InputStream inputStream;

    public HttpBodyAnnotationAnnotatedParameterValueMatcher(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        this.inputStream = inputStream;
    }

    @Override
    public ParameterValue match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("Does not RequestBody annotated. parameter : `{}`", parameter));
        }

        return ParameterValue.from(inputStream);
    }
}
