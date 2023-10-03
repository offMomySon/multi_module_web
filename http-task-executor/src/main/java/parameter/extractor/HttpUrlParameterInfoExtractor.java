package parameter.extractor;

import java.lang.reflect.Parameter;
import java.util.Objects;
import lombok.Getter;

public interface HttpUrlParameterInfoExtractor {
    HttpUrlParameterInfo extract(Parameter parameter);

    @Getter
    class HttpUrlParameterInfo {
        private final String parameterName;
        private final String defaultValue;
        private final boolean required;

        public HttpUrlParameterInfo(String parameterName, String defaultValue, boolean required) {
            Objects.requireNonNull(parameterName);
            this.parameterName = parameterName;
            this.defaultValue = defaultValue;
            this.required = required;
        }
    }
}
