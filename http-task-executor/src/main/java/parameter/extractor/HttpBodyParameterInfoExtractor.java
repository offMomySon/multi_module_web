package parameter.extractor;

import java.lang.reflect.Parameter;

public interface HttpBodyParameterInfoExtractor {
    HttpBodyParameterInfo extract(Parameter parameter);

    class HttpBodyParameterInfo {
        private final boolean required;

        public HttpBodyParameterInfo(boolean required) {
            this.required = required;
        }
    }
}
