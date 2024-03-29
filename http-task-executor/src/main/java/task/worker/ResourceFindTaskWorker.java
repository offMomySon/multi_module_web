package task.worker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterAndValueAssigneeType;

@Slf4j
public class ResourceFindTaskWorker implements EndPointTaskWorker {
    private final WorkerResultType workerResultType;
    private final Path resourcePath;

    public ResourceFindTaskWorker(WorkerResultType workerResultType, Path resourcePath) {
        Objects.requireNonNull(workerResultType);
        Objects.requireNonNull(resourcePath);
        this.workerResultType = workerResultType;
        this.resourcePath = resourcePath;
    }

    @Override
    public ParameterAndValueAssigneeType[] getParameterTypeInfos() {
        return new ParameterAndValueAssigneeType[0];
    }

    @Override
    public Optional<Object> execute(Object[] params) {
        log.info("resourcePath : `{}`", resourcePath);

        if (Files.notExists(resourcePath)) {
            log.info("does not exist resource. ResourcePath : `{}`", resourcePath);
            throw new RuntimeException(MessageFormat.format("does not exist resource. ResourcePath : `{}`", resourcePath));
        }

        return Optional.of(resourcePath);
    }
}
