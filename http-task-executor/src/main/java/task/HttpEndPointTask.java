package task;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import parameter.matcher.ParameterAndValueAssigneeType;
import vo.ContentType;

public interface HttpEndPointTask {
    ParameterAndValueAssigneeType[] getParameterTypeInfos();

    Optional<HttpTaskResult> execute(Object[] params);

    class HttpTaskResult {
        private final ContentType contentType;
        private final InputStream content;

        public HttpTaskResult(ContentType contentType, InputStream content) {
            Objects.requireNonNull(contentType);
            Objects.requireNonNull(content);
            this.contentType = contentType;
            this.content = content;
        }

        public ContentType getContentType() {
            return contentType;
        }

        public InputStream getContent() {
            return content;
        }
    }
}
