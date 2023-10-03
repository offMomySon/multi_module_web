package parameter.matcher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import parameter.extractor.HttpBodyParameterInfoExtractor;
import parameter.extractor.HttpBodyParameterInfoExtractor.HttpBodyParameterInfo;
import static com.main.util.IoUtils.createBufferedInputStream;

public class HttpBodyParameterValueMatcher implements MethodParameterValueMatcher {
    private final HttpBodyParameterInfoExtractor parameterInfoExtractor;
    private final InputStream inputStream;

    public HttpBodyParameterValueMatcher(HttpBodyParameterInfoExtractor parameterInfoExtractor, InputStream inputStream) {
        Objects.requireNonNull(parameterInfoExtractor);
        Objects.requireNonNull(inputStream);
        this.parameterInfoExtractor = parameterInfoExtractor;
        this.inputStream = inputStream;
    }

    @Override
    public Optional<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        HttpBodyParameterInfo bodyParameterInfo = parameterInfoExtractor.extract(parameter);
        boolean required = bodyParameterInfo.isRequired();

        boolean doesNotPossibleValueMatch = required && isEmpty(inputStream);
        if (doesNotPossibleValueMatch) {
            throw new RuntimeException("Does not possible value match. InputStream is empty.");
        }

        String content = readBody(this.inputStream);
        return Optional.of(content);
    }

    private static boolean isEmpty(InputStream inputStream) {
        try {
            return inputStream.available() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
