package matcher;

import converter.ValueConverter;
import converter.PathValueConverter;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.segment.PathUrl2;
import matcher.segment.PathVariableValue;
import task.HttpConvertEndPointTask;
import task.HttpEndPointTask;
import task.worker.ResourceFindTaskWorker;
import task.worker.WorkerResultType;

@Slf4j
public class StaticResourceEndPointTaskMatcher implements EndpointTaskMatcher {
    private static final RequestMethod REQUEST_METHOD = RequestMethod.GET;

    private final PathUrl2 endPointUrl;
    private final Path resourcePath;

    public StaticResourceEndPointTaskMatcher(PathUrl2 endPointUrl, Path resourcePath) {
        Objects.requireNonNull(endPointUrl);
        Objects.requireNonNull(resourcePath);
        this.endPointUrl = endPointUrl;
        this.resourcePath = resourcePath;
    }

    @Override
    public Optional<MatchedEndPoint> match(RequestMethod requestMethod, PathUrl2 requestUrl) {
        boolean doesNotResourceMethod = !requestMethod.equals(REQUEST_METHOD);
        if (doesNotResourceMethod) {
            return Optional.empty();
        }

        boolean doesNotMatchEndPointUrl = !endPointUrl.equals(requestUrl);
        if (doesNotMatchEndPointUrl) {
            return Optional.empty();
        }

        log.info("Matched. requestUrl : `{}`, endPointUrl : `{}`, resourcePath : `{}`", requestUrl, endPointUrl, resourcePath);
        String name = resourcePath.toFile().getName();
        WorkerResultType workerResultType = WorkerResultType.findByFileName(name);
        ResourceFindTaskWorker resourceFindTask = new ResourceFindTaskWorker(workerResultType, resourcePath);
        ValueConverter valueConverter = new PathValueConverter();
        HttpEndPointTask httpEndPointTask = new HttpConvertEndPointTask(vo.ContentType.APPLICATION_JSON, valueConverter, resourceFindTask);
        MatchedEndPoint matchedEndPoint = new MatchedEndPoint(httpEndPointTask, PathVariableValue.empty());
        return Optional.of(matchedEndPoint);
    }

    @Override
    public String toString() {
        return "StaticResourceEndPointTaskMatcher{" +
            "endPointUrl=" + endPointUrl +
            ", resourcePath=" + resourcePath +
            '}';
    }
}
