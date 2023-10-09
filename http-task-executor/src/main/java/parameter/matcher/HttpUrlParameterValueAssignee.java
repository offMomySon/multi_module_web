package parameter.matcher;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import parameter.UrlParameterValues;
import parameter.extractor.HttpUrlParameterInfoExtractor;
import static parameter.extractor.HttpUrlParameterInfoExtractor.HttpUrlParameterInfo;

public class HttpUrlParameterValueAssignee implements ParameterValueAssignee {
    private final Function<Parameter, HttpUrlParameterInfo> extractFunction;
    private final UrlParameterValues urlParameterValues;

    public HttpUrlParameterValueAssignee(Function<Parameter, HttpUrlParameterInfo> extractFunction, UrlParameterValues urlParameterValues) {
        Objects.requireNonNull(extractFunction);
        Objects.requireNonNull(urlParameterValues);
        this.extractFunction = extractFunction;
        this.urlParameterValues = urlParameterValues;
    }

    @Override
    public Optional<?> assign(Parameter parameter) {
        Objects.requireNonNull(parameter);

        HttpUrlParameterInfo urlParameterInfo = extractFunction.apply(parameter);
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
