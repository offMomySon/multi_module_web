package com.main.task.matcher;

import com.main.util.AnnotationUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import annotation.RequestBody;
import parameter.MethodParameterValueMatcher;
import static com.main.util.IoUtils.createBufferedInputStream;

// 4. todo [annotation]
// input stream 에 RequestBody 어노테이션이 존재하는지 검증한다.
// 해당 어노테이팅이 필요한지 체크하고
// 필요하다면 annotation 모듈의 역할로 처리가 가능하게 수정하자.
public class HttpBodyAnnotationAnnotatedParameterValueMatcher implements MethodParameterValueMatcher {
    private static final Class<RequestBody> REQUEST_BODY_CLASS = RequestBody.class;

    private final InputStream bodyInputStream;
    private String body;

    public HttpBodyAnnotationAnnotatedParameterValueMatcher(InputStream bodyInputStream) {
        Objects.requireNonNull(bodyInputStream);
        this.bodyInputStream = bodyInputStream;
    }

    @Override
    public Optional<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<RequestBody> optionalRequestBody = AnnotationUtils.find(parameter, REQUEST_BODY_CLASS);
        if (optionalRequestBody.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("Does not RequestBody annotated. parameter : `{}`", parameter));
        }

        if (Objects.isNull(this.body)) {
            this.body = readBody(this.bodyInputStream);
        }

        return Optional.of(this.body);
    }

    private static String readBody(InputStream bodyInputStream) {
        BufferedInputStream newBodyInputStream = createBufferedInputStream(bodyInputStream);
        byte[] readAllBytes = readAllBytes(newBodyInputStream);
        return new String(readAllBytes);
    }

    private static byte[] readAllBytes(BufferedInputStream bufferedInputStream) {
        try {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
