package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import parameter.UrlParameters;
import parameter.extractor.HttpUrlParameterInfoExtractor;
import static parameter.extractor.HttpUrlParameterInfoExtractor.HttpUrlParameterInfo;

public class HttpUrlParameterValueMatcher implements ParameterValueMatcher {
    private final HttpUrlParameterInfoExtractor parameterInfoExtractor;
    private final UrlParameters urlParameters;

    public HttpUrlParameterValueMatcher(HttpUrlParameterInfoExtractor parameterInfoExtractor, UrlParameters urlParameters) {
        Objects.requireNonNull(parameterInfoExtractor);
        Objects.requireNonNull(urlParameters);
        this.parameterInfoExtractor = parameterInfoExtractor;
        this.urlParameters = urlParameters;
    }

    @Override
    public Optional<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        HttpUrlParameterInfo urlParameterInfo = parameterInfoExtractor.extract(parameter);
        String parameterName = urlParameterInfo.getParameterName();
        String defaultValue = urlParameterInfo.getDefaultValue();
        boolean required = urlParameterInfo.isRequired();

        String matchValue = urlParameters.getOrDefault(parameterName, defaultValue);
        Optional<String> optionalMatchValue = Optional.ofNullable(matchValue);

        boolean doesNotPossibleMatchValue = optionalMatchValue.isEmpty() && required;
        if (doesNotPossibleMatchValue) {
            throw new RuntimeException("Does not Possible match value, value must be exist.");
        }

        return optionalMatchValue;
    }
}
