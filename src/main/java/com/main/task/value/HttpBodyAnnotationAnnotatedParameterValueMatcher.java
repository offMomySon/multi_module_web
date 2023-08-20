package com.main.task.value;

import com.main.util.AnnotationUtils;
import com.main.util.IoUtils;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.RequestBody;
import matcher.converter.BodyContent;

public class HttpBodyAnnotationAnnotatedParameterValueMatcher implements MethodParameterValueMatcher {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;

    private final String body;

    public HttpBodyAnnotationAnnotatedParameterValueMatcher(String body) {
        Objects.requireNonNull(body);
        this.body = body;
    }

    public static HttpBodyAnnotationAnnotatedParameterValueMatcher from(BodyContent bodyContent) {
        Objects.requireNonNull(bodyContent);

        String body = bodyContent.getValue();
        return new HttpBodyAnnotationAnnotatedParameterValueMatcher(body);
    }

    @Override
    public ParameterValue<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("Does not RequestBody annotated. parameter : `{}`", parameter));
        }

        return ParameterValue.from(body);
    }
}
