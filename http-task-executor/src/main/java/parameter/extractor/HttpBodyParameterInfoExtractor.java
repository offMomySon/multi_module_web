package parameter.extractor;

import java.lang.reflect.Parameter;
import lombok.Getter;

public interface HttpBodyParameterInfoExtractor {
    HttpBodyParameterInfo extract(Parameter parameter);

    @Getter
    class HttpBodyParameterInfo {
        private final boolean required;

        public HttpBodyParameterInfo(boolean required) {
            this.required = required;
        }
    }
}
