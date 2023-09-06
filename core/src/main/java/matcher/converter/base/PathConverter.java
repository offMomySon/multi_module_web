package matcher.converter.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathConverter implements Converter<Path>{

    @Override
    public InputStream convertToInputStream(Path path) {
        Objects.requireNonNull(path);
        log.info("path : `{}`", path);

        return getPathInputStream(path);
    }

    private static InputStream getPathInputStream(Path path){
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
