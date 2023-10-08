package task.worker;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import parameter.matcher.ParameterAndValueMatcherType;
import task.SystemResourceFinder;

public class SystemResourceFileFindTaskWorker implements EndPointTaskWorker {
    private final SystemResourceFinder systemResourceFinder;
    private final String resourcePath;

    public SystemResourceFileFindTaskWorker(SystemResourceFinder systemResourceFinder, String resourcePath) {
        Objects.requireNonNull(systemResourceFinder);
        if (Objects.isNull(resourcePath) || resourcePath.isBlank()) {
            throw new RuntimeException("does not exist resourcePath.");
        }
        this.systemResourceFinder = systemResourceFinder;
        this.resourcePath = resourcePath;
    }

    @Override
    public ParameterAndValueMatcherType[] getParameterTypeInfos() {
        return new ParameterAndValueMatcherType[0];
    }

    @Override
    public Optional<Object> execute(Object[] params) {
        Optional<Path> optionalPath = systemResourceFinder.findFile(resourcePath);

        if (optionalPath.isEmpty()) {
            Optional.empty();
        }

        Path path = optionalPath.get();
        return Optional.of(path);
    }
}