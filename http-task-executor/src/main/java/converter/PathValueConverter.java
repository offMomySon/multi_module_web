package converter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathValueConverter implements ValueConverter {

    @Override
    public InputStream convertToInputStream(Object path) {
        Objects.requireNonNull(path);
        return getPathInputStream((Path)path);
    }

    @Override
    public Object convertToClazz(String value) {
        Objects.requireNonNull(value);
        return Paths.get(value);
    }

    private static InputStream getPathInputStream(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
