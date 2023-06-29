package com.main.extractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.annotation.RequestBody;
import matcher.converter.BodyContent;
import util.AnnotationUtils;

@Slf4j
public class BodyParameterValueExtractor implements ParameterValueExtractor {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;

    private final BodyContent bodyContent;
    private final Parameter parameter;

    public BodyParameterValueExtractor(BodyContent bodyContent, Parameter parameter) {
        Objects.requireNonNull(bodyContent);
        Objects.requireNonNull(parameter);
        this.bodyContent = bodyContent;
        this.parameter = parameter;
    }

    @Override
    public ExtractValue extract() {
        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            throw new IllegalArgumentException("requestBody 만 받을 수 있습니다.");
        }

        RequestBody requestBody = optionalRequestBody.get();
        boolean isEmptyBody = bodyContent.isEmpty();

        boolean doesNotPossibleCreate = requestBody.required() && isEmptyBody;
        if (doesNotPossibleCreate) {
            throw new RuntimeException("메세지가 비어 생성할 수 없습니다.");
        }

        boolean isPossibleEmptyBody = !requestBody.required() && isEmptyBody;
        if (isPossibleEmptyBody) {
            return new ExtractValue(parameter.getType(), Optional.empty());
        }

        String body = bodyContent.getValue();
        return new ExtractValue(parameter.getType(), Optional.of(body));
    }
}
