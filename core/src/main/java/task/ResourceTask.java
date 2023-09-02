package task;

import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceTask implements HttpTask {
    private final String resourceUrl;

    public ResourceTask(String resourceUrl) {
        Objects.requireNonNull(resourceUrl);
        this.resourceUrl = resourceUrl;
    }

    @Override
    public Parameter[] getExecuteParameters() {
        return new Parameter[0];
    }

    @Override
    public Optional<Object> execute(Object[] params) {
        Path resourcePath = Path.of(resourceUrl);
        log.info("resourcePath : `{}`", resourcePath);

        if (Files.notExists(resourcePath)) {
            log.info("file does not exist");
            throw new RuntimeException("path does not exist");
        }

        return Optional.of(resourcePath);
    }
}
