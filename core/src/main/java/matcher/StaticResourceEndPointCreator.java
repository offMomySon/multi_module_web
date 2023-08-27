package matcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.segment.PathUrl;
import util.FileSystemUtil;

@Slf4j
public class StaticResourceEndPointCreator {
    private static final String DIRECTORY_DELIMITER = "/";
    private final Path resourceDirectory;

    public StaticResourceEndPointCreator(Path resourceDirectory) {
        Objects.requireNonNull(resourceDirectory);
        log.info("resourceDirectory : {}", resourceDirectory);
        this.resourceDirectory = resourceDirectory.normalize();
    }

    public static StaticResourceEndPointCreator from(Class<?> clazz, String resourcePackage) {
        Objects.requireNonNull(clazz);
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzPath = FileSystemUtil.getClazzRootPath(clazz);
        Path projectPackageDirectory = clazzPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage);
        resourceDirectory = resourceDirectory.normalize();

        return new StaticResourceEndPointCreator(resourceDirectory);
    }

    private static List<PathUrl> extractResourceUrls(Path resourceDirectory) {
        try (Stream<Path> fileWalk = Files.walk(resourceDirectory)) {
            return fileWalk
                .filter(path -> !Files.isDirectory(path))
                .map(resourcePath -> createResourceUrl(resourceDirectory, resourcePath))
                .peek(resourceUrl -> log.info("resourceUrl : {}", resourceUrl))
                .map(r -> PathUrl.from(r.toString()))
                .collect(Collectors.toUnmodifiableList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<StaticResourceEndPointJavaMethodMatcher> create(){
        return extractResourceUrls(resourceDirectory)
            .stream()
            .map(StaticResourceEndPointJavaMethodMatcher::new)
            .collect(Collectors.toUnmodifiableList());
    }

    private static Path createResourceUrl(Path resourceDirectory, Path resourcePath) {
        Path packageResourcePath = resourceDirectory.relativize(resourcePath);
        return Path.of(DIRECTORY_DELIMITER).resolve(packageResourcePath);
    }
}
