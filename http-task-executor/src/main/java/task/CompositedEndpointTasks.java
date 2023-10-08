package task;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import matcher.MatchedEndPointTaskWorker2;
import matcher.RequestMethod;
import matcher.segment.PathUrl;

public class CompositedEndpointTasks implements EndPointTask {
    private final List<EndPointTask> endPointTasks;

    public CompositedEndpointTasks(List<EndPointTask> endPointTasks) {
        if (Objects.isNull(endPointTasks)) {
            throw new RuntimeException("Invalid parameter. endPointTasks is null.");
        }

        List<EndPointTask> newEndPointTasks = endPointTasks.stream()
            .filter(o -> !Objects.isNull(o))
            .collect(Collectors.toUnmodifiableList());

        if (newEndPointTasks.isEmpty()) {
            throw new RuntimeException("newMethodResovler is empty.");
        }

        this.endPointTasks = newEndPointTasks;
    }

    public Optional<MatchedEndPointTaskWorker2> match(RequestMethod requestMethod, PathUrl requestUrl) {
        return endPointTasks.stream()
            .map(endPointTask -> endPointTask.match(requestMethod, requestUrl))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }
}
