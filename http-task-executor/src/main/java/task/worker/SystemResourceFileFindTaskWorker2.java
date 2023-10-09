package task.worker;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import parameter.matcher.ParameterAndValueAssigneeType;
import task.SystemResourceFinder;

public class SystemResourceFileFindTaskWorker2 implements EndPointTaskWorker2 {
    private final SystemResourceFinder systemResourceFinder;
    private final String resourcePath;

    public SystemResourceFileFindTaskWorker2(SystemResourceFinder systemResourceFinder, String resourcePath) {
        Objects.requireNonNull(systemResourceFinder);
        if (Objects.isNull(resourcePath) || resourcePath.isBlank()) {
            throw new RuntimeException("does not exist resourcePath.");
        }
        this.systemResourceFinder = systemResourceFinder;
        this.resourcePath = resourcePath;
    }

    @Override
    public ParameterAndValueAssigneeType[] getParameterTypeInfos() {
        return new ParameterAndValueAssigneeType[0];
    }

    @Override
    public EndPointWorkerResult execute(Object[] params) {
        Optional<Path> optionalFoundResource = systemResourceFinder.findFile(resourcePath);

        if (optionalFoundResource.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("Fail to find resource. ResourcePath : `{}`", resourcePath));
        }

        Path foundResource = optionalFoundResource.get();
        WorkerResultType workerResultType = WorkerResultType.findByPath(foundResource);
        return new EndPointWorkerResult(workerResultType, foundResource);
    }
}