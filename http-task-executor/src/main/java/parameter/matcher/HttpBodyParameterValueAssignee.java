package parameter.matcher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import parameter.extractor.HttpBodyParameterInfoExtractor;
import parameter.extractor.HttpBodyParameterInfoExtractor.HttpBodyParameterInfo;
import static com.main.util.IoUtils.createBufferedInputStream;

public class HttpBodyParameterValueAssignee implements ParameterValueAssignee {
    private final Function<Parameter, HttpBodyParameterInfo> parameterInfoExtractorFunction;
    private final InputStream inputStream;

    public HttpBodyParameterValueAssignee(Function<Parameter, HttpBodyParameterInfo> parameterInfoExtractorFunction, InputStream inputStream) {
        Objects.requireNonNull(parameterInfoExtractorFunction);
        Objects.requireNonNull(inputStream);
        this.parameterInfoExtractorFunction = parameterInfoExtractorFunction;
        this.inputStream = inputStream;
    }

    @Override
    public Optional<?> assign(Parameter parameter) {
        Objects.requireNonNull(parameter);

        HttpBodyParameterInfo bodyParameterInfo = parameterInfoExtractorFunction.apply(parameter);
        boolean required = bodyParameterInfo.isRequired();

        boolean doesNotPossibleValueMatch = required && isEmpty(inputStream);
        if (doesNotPossibleValueMatch) {
            throw new RuntimeException("Does not possible value match. InputStream is empty.");
        }

        String content = readBody(this.inputStream);
        doesNotPossibleValueMatch = required && content.isBlank();
        if (doesNotPossibleValueMatch) {
            throw new RuntimeException("Does not possible value match. InputStream is empty.");
        }

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
