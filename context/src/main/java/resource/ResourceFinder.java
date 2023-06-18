package resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import util.FileSystemUtil;

@Slf4j
public class ResourceFinder {
    private static final String DIRECTORY_DELIMITER = "/";

    private final Path resourceDirectory;

    public ResourceFinder(Path resourceDirectory) {
        Objects.requireNonNull(resourceDirectory);
        log.info("resourceDirectory : {}", resourceDirectory);
        this.resourceDirectory = resourceDirectory.normalize();
    }

    public static ResourceFinder from(Class<?> clazz, String resourcePackage) {
        Objects.requireNonNull(clazz);
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzPath = FileSystemUtil.getClazzRootPath(clazz);
        Path projectPackageDirectory = clazzPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage);

        return new ResourceFinder(resourceDirectory);
    }

    public ResourceUrls extractResourceUrls() {
        try (Stream<Path> fileWalk = Files.walk(resourceDirectory)) {
            Set<Path> urls = fileWalk
                .filter(path -> !Files.isDirectory(path))
                .map(resourcePath -> createResourceUrl(resourceDirectory, resourcePath))
                .peek(resourceUrl -> log.info("resourceUrl : {}", resourceUrl))
                .collect(Collectors.toUnmodifiableSet());
            return new ResourceUrls(urls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Path createResourceUrl(Path resourceDirectory, Path resourcePath) {
        Path packageResourcePath = resourceDirectory.relativize(resourcePath);
        return Path.of(DIRECTORY_DELIMITER).resolve(packageResourcePath);
    }

    public Optional<Path> findResource(Path resourceUrl) {
        if (Objects.isNull(resourceUrl)) {
            return Optional.empty();
        }

        resourceUrl = resourceUrl.normalize();
        if (resourceUrl.startsWith(DIRECTORY_DELIMITER)) {
            resourceUrl = Path.of(resourceUrl.toString().substring(1));
        }

        Path canonicalResourcePath = resourceDirectory.resolve(resourceUrl);

        if (Files.notExists(canonicalResourcePath)) {
            log.info("file does not exist");
            return Optional.empty();
        }
        return Optional.of(canonicalResourcePath);
    }
}
