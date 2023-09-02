package matcher.converter.base;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathConverter implements Converter<Path>{

    @Override
    public InputStream convertToInputStream(Path path) {
        Objects.requireNonNull(path);
        log.info("path : `{}`", path);

        String newPath = path.toString();
        String relativePath = newPath.startsWith("/") ? newPath.substring(1) : newPath;
        log.info("relativePath : `{}`", relativePath);

        return Thread.currentThread().getContextClassLoader().getResourceAsStream(relativePath);
    }
}
