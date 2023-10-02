package matcher;

import converter.ValueConverter;
import converter.PathValueConverter;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import task.HttpConvertEndPointTask;
import task.HttpEndPointTask;
import task.endpoint.ResourceFindTask;
import vo.ContentType;

@Slf4j
public class StaticResourceEndPointTaskMatcher implements EndpointTaskMatcher {
    private static final RequestMethod REQUEST_METHOD = RequestMethod.GET;

    private final PathUrl endPointUrl;
    private final Path resourcePath;

    public StaticResourceEndPointTaskMatcher(PathUrl endPointUrl, Path resourcePath) {
        Objects.requireNonNull(endPointUrl);
        Objects.requireNonNull(resourcePath);
        this.endPointUrl = endPointUrl;
        this.resourcePath = resourcePath;
    }

    @Override
    public Optional<MatchedEndPoint> match(RequestMethod requestMethod, PathUrl requestUrl) {
        boolean doesNotResourceMethod = !requestMethod.equals(REQUEST_METHOD);
        if (doesNotResourceMethod) {
            return Optional.empty();
        }

        boolean doesNotMatchEndPointUrl = !endPointUrl.equals(requestUrl);
        if (doesNotMatchEndPointUrl) {
            return Optional.empty();
        }

        log.info("Matched. requestUrl : `{}`, endPointUrl : `{}`, resourcePath : `{}`", requestUrl, endPointUrl, resourcePath);
        ResourceFindTask resourceFindTask = new ResourceFindTask(resourcePath);
        ValueConverter valueConverter = new PathValueConverter();
        HttpEndPointTask httpEndPointTask = new HttpConvertEndPointTask(ContentType.APPLICATION_JSON, valueConverter, resourceFindTask);
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
