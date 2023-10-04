package task.endpoint;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import parameter.matcher.ParameterAndValueMatcherType;

@Slf4j
public class ResourceFindTask implements EndPointTask {
    private final Path resourcePath;

    public ResourceFindTask(Path resourcePath) {
        Objects.requireNonNull(resourcePath);
        this.resourcePath = resourcePath;
    }

    @Override
    public ParameterAndValueMatcherType[] getParameterTypeInfos() {
        return new ParameterAndValueMatcherType[0];
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
