package matcher;

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
    private final ResourceUrls resourceUrls;

    public ResourceFinder(Path resourceDirectory, ResourceUrls resourceUrls) {
        Objects.requireNonNull(resourceDirectory);
        Objects.requireNonNull(resourceUrls);
        log.info("resourceDirectory : {}", resourceDirectory);
        log.info("resourceUrls : {}", resourceUrls);
        this.resourceDirectory = resourceDirectory.normalize();
        this.resourceUrls = resourceUrls;
    }

    public static ResourceFinder from(Class<?> clazz, String resourcePackage) {
        Objects.requireNonNull(clazz);
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzPath = FileSystemUtil.getClazzRootPath(clazz);
        Path projectPackageDirectory = clazzPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage);
        resourceDirectory = resourceDirectory.normalize();

        ResourceUrls resourceUrls = extractResourceUrls(resourceDirectory);

        return new ResourceFinder(resourceDirectory, resourceUrls);
    }

    private static ResourceUrls extractResourceUrls(Path resourceDirectory) {
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

    public Optional<Path> findResource(Path requestUrl) {
        if (Objects.isNull(requestUrl)) {
            return Optional.empty();
        }
        requestUrl = requestUrl.normalize();

        boolean doesNotExistMatchUrl = !resourceUrls.contain(requestUrl);
        if (doesNotExistMatchUrl) {
            log.info("does not exist MatchUrl.");
            return Optional.empty();
        }

        if (requestUrl.startsWith(DIRECTORY_DELIMITER)) {
            requestUrl = Path.of(requestUrl.toString().substring(1));
        }

        Path canonicalResourcePath = resourceDirectory.resolve(requestUrl);

        if (Files.notExists(canonicalResourcePath)) {
            log.info("file does not exist");
            return Optional.empty();
        }
        return Optional.of(canonicalResourcePath);
    }
}
