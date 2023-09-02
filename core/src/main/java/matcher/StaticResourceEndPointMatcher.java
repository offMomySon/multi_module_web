package matcher;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import task.ResourceTask;

@Slf4j
public class StaticResourceEndPointMatcher implements EndpointMatcher {
    private static final RequestMethod REQUEST_METHOD = RequestMethod.GET;

    private final Method method;
    private final PathUrl pathUrl;
    private final Path resourcePath;
    private final String pathVariableKey;

    public StaticResourceEndPointMatcher(Method method, PathUrl pathUrl, Path resourcePath, String pathVariableKey) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(pathUrl);
        Objects.requireNonNull(resourcePath);
        Objects.requireNonNull(pathVariableKey);
        this.method = method;
        this.pathUrl = pathUrl;
        this.resourcePath = resourcePath;
        this.pathVariableKey = pathVariableKey;
    }

    @Override
    public Optional<MatchedHttpTask> match(RequestMethod requestMethod, PathUrl requestUrl) {
        boolean doesNotResourceMethod = !requestMethod.equals(REQUEST_METHOD);
        if (doesNotResourceMethod) {
            return Optional.empty();
        }

        boolean doesNotEqualRequestUrl = !pathUrl.equals(requestUrl);
        if(doesNotEqualRequestUrl){
            return Optional.empty();
        }

        log.info("found match. pathUrl : `{}`, requestUrl : `{}`", pathUrl, requestUrl);
        ResourceTask resourceTask = new ResourceTask(resourcePath.toString());
        return Optional.of(new MatchedHttpTask(resourceTask, PathVariableValue.empty()));
    }
}
