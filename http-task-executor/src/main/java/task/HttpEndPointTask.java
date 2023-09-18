package task;

import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import vo.ContentType;

public interface HttpEndPointTask {
    Parameter[] getExecuteParameters();

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
