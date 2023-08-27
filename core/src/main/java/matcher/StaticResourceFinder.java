package matcher;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import matcher.segment.PathUrl;

@Slf4j
public class StaticResourceFinder {
    public Path find(PathUrl pathUrl) {
        String resourceUrl = pathUrl.toAbsolutePath();
        Path path = Path.of(resourceUrl);
        log.info("path : `{}`", path);

        if (Files.notExists(path)) {
            log.info("file does not exist");
            throw new RuntimeException("path does not exist");
        }
        return path;
    }
}
