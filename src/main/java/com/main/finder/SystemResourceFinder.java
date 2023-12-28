package com.main.finder;

import com.main.util.FileSystemUtil;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemResourceFinder {
    private final Path resourceDirectory;

    public SystemResourceFinder(Path resourceDirectory) {
        Objects.requireNonNull(resourceDirectory);
        this.resourceDirectory = resourceDirectory;
    }

    public static SystemResourceFinder fromPackage(Class<?> rootClazz, String resourcePackage) {
        Objects.requireNonNull(rootClazz);
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzPath = FileSystemUtil.getClazzRootPath(rootClazz);
        Path projectPackageDirectory = clazzPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage).normalize();
        log.info("clazzPath : {}", clazzPath);
        log.info("projectPackageDirectory : {}", projectPackageDirectory);
        log.info("resourceDirectory : {}", resourceDirectory);
        return new SystemResourceFinder(resourceDirectory);
    }

    public boolean isExistFile(String url) {
        return findFile(url).isPresent();
    }

    public boolean doesNotExistFile(String url) {
        return !isExistFile(url);
    }

    public Optional<Path> findFile(String url) {
        if (Objects.isNull(url) || url.isBlank()) {
            return Optional.empty();
        }

        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        Path resource = resourceDirectory.resolve(url);

        if (Files.notExists(resource)) {
            log.info("Does not exist file. file: `{}`", resource);
            return Optional.empty();
        }

        if (!Files.isRegularFile(resource, LinkOption.NOFOLLOW_LINKS)) {
            log.info("Does not regularFile. file: `{}`", resource);
            return Optional.empty();
        }

        if (Files.isDirectory(resource)) {
            log.info("Resource is directory. file: `{}`", resource);
            return Optional.empty();
        }

        log.info("Resource is exist. resource: `{}`", resource);
        return Optional.of(resource);
    }
}
