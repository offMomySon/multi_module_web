package com.main.finder;

import com.main.util.FileSystemUtil;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemResourceFinder2 {
    private static final String DIRECTORY_DELIMITER = "/";

    private final Path resourceDirectory;
    private final String prefix;

    private SystemResourceFinder2(@NonNull Path resourceDirectory, @NonNull String prefix) {
        this.resourceDirectory = resourceDirectory;
        this.prefix = prefix;
    }

    public static SystemResourceFinder2 fromPackage(@NonNull Class<?> clazz, @NonNull String resourcePackage, @NonNull String prefix) {
        if (resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzRootPath = FileSystemUtil.getClazzRootPath(clazz);
        Path projectPackageDirectory = clazzRootPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage).normalize();
        log.info("clazzPath : {}", clazzRootPath);
        log.info("projectPackageDirectory : {}", projectPackageDirectory);
        log.info("resourceDirectory : {}", resourceDirectory);
        return new SystemResourceFinder2(resourceDirectory, prefix);
    }

    public Optional<Path> findFile(@NonNull Path path) {
        String pathUrl = path.toString();
        if (!pathUrl.startsWith(prefix)) {
            return Optional.empty();
        }
        pathUrl = pathUrl.substring(prefix.length());

        if (pathUrl.startsWith(DIRECTORY_DELIMITER)) {
            pathUrl = pathUrl.substring(1);
        }

        log.info("pathUrl : {}", pathUrl);

        Path resource = resourceDirectory.resolve(pathUrl);
        log.info(String.valueOf(resource));

        if (Files.notExists(resource)) {
            log.info("Does not exist file.");
            return Optional.empty();
        }

        if (!Files.isRegularFile(resource, LinkOption.NOFOLLOW_LINKS)) {
            log.info("Does not regularFile.");
            return Optional.empty();
        }

        if (Files.isDirectory(resource)) {
            log.info("Resource is directory.");
            return Optional.empty();
        }

        log.info("Resource is exist.");
        return Optional.of(resource);
    }

    public static Method getFindFileMethod() {
        try {
            return SystemResourceFinder2.class.getMethod("findFile", Path.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
