package task;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.MatchedEndPointTaskWorker2;
import matcher.RequestMethod;
import matcher.segment.PathUrl2;
import matcher.segment.PathVariableValue;
import task.worker.SystemResourceFileFindTaskWorker2;

@Slf4j
public class ResourceEndPointFindTask2 implements EndPointTask2 {
    private final SystemResourceFinder systemResourceFinder;
    private final String urlPrefix;

    public ResourceEndPointFindTask2(SystemResourceFinder systemResourceFinder, String urlPrefix) {
        Objects.requireNonNull(systemResourceFinder);
        Objects.requireNonNull(urlPrefix);
        this.systemResourceFinder = systemResourceFinder;
        this.urlPrefix = urlPrefix;
    }

    @Override
    public Optional<MatchedEndPointTaskWorker2> match(RequestMethod requestMethod, PathUrl2 requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        if (RequestMethod.GET != requestMethod) {
            return Optional.empty();
        }

        String newRequestUrl = requestUrl.toAbsolutePath();
        if(!newRequestUrl.startsWith(urlPrefix)){
            return Optional.empty();
        }

        int excludeUrlPrefixIndex = newRequestUrl.indexOf(urlPrefix) + urlPrefix.length();
        String resourcePath = newRequestUrl.substring(excludeUrlPrefixIndex);

        if (systemResourceFinder.doesNotExistFile(resourcePath)) {
            return Optional.empty();
        }

        SystemResourceFileFindTaskWorker2 taskWorker = new SystemResourceFileFindTaskWorker2(this.systemResourceFinder, resourcePath);
        PathVariableValue emptyPathVariableValue = PathVariableValue.empty();
        MatchedEndPointTaskWorker2 matchedEndPoint = new MatchedEndPointTaskWorker2(taskWorker, emptyPathVariableValue);
        return Optional.of(matchedEndPoint);
    }
}
