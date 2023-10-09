package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import parameter.UrlParameterValues;
import parameter.extractor.HttpUrlParameterInfoExtractor;
import static parameter.extractor.HttpUrlParameterInfoExtractor.HttpUrlParameterInfo;

public class HttpUrlParameterValueAssignee implements ParameterValueAssignee {
    private final HttpUrlParameterInfoExtractor parameterInfoExtractor;
    private final UrlParameterValues urlParameterValues;

    public HttpUrlParameterValueAssignee(HttpUrlParameterInfoExtractor parameterInfoExtractor, UrlParameterValues urlParameterValues) {
        Objects.requireNonNull(parameterInfoExtractor);
        Objects.requireNonNull(urlParameterValues);
        this.parameterInfoExtractor = parameterInfoExtractor;
        this.urlParameterValues = urlParameterValues;
    }

    @Override
    public Optional<?> assign(Parameter parameter) {
        Objects.requireNonNull(parameter);

        HttpUrlParameterInfo urlParameterInfo = parameterInfoExtractor.extract(parameter);
        String parameterName = urlParameterInfo.getParameterName();
        String defaultValue = urlParameterInfo.getDefaultValue();
        boolean required = urlParameterInfo.isRequired();

        String matchValue = urlParameterValues.getOrDefault(parameterName, defaultValue);
        Optional<String> optionalMatchValue = Optional.ofNullable(matchValue);

        boolean doesNotPossibleMatchValue = optionalMatchValue.isEmpty() && required;
        if (doesNotPossibleMatchValue) {
            throw new RuntimeException("Does not Possible match value, value must be exist.");
        }

        return optionalMatchValue;
    }
}
