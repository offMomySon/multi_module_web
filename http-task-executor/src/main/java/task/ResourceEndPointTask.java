package task;

import java.util.Objects;
import java.util.Optional;
import matcher.MatchedEndPoint2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;
import matcher.segment.PathVariableValue;
import task.worker.SystemResourceFileFindTaskWorker;

public class ResourceEndPointTask implements EndPointTask {
    private final SystemResourceFinder systemResourceFinder;
    private final String urlPrefix;

    public ResourceEndPointTask(SystemResourceFinder systemResourceFinder, String urlPrefix) {
        Objects.requireNonNull(systemResourceFinder);
        Objects.requireNonNull(urlPrefix);
        this.systemResourceFinder = systemResourceFinder;
        this.urlPrefix = urlPrefix;
    }

    @Override
    public Optional<MatchedEndPoint2> match(RequestMethod requestMethod, PathUrl requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (RequestMethod.GET != requestMethod) {
            return Optional.empty();
        }

        String resourcePath = urlPrefix + requestUrl.toAbsolutePath();
        if (systemResourceFinder.doesNotExistFile(resourcePath)) {
            return Optional.empty();
        }

        SystemResourceFileFindTaskWorker taskWorker = new SystemResourceFileFindTaskWorker(this.systemResourceFinder, resourcePath);
        PathVariableValue emptyPathVariableValue = PathVariableValue.empty();
        MatchedEndPoint2 matchedEndPoint = new MatchedEndPoint2(taskWorker, emptyPathVariableValue);
        return Optional.of(matchedEndPoint);
    }
}
