package matcher;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.annotation.PathVariable;
import matcher.segment.PathUrl;
import util.FileSystemUtil;

@Slf4j
public class StaticResourceEndPointCreator {
    private static final String PATH_VARIABLE_KEY = "pathUrl";

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


    public List<StaticResourceEndPointMatcher> create() {
        Method staticResourceFindMethod = getStaticResourceFindMethod();

        List<Path> foundFiles = findFilePath(resourceDirectory);

        List<ResourcePath> resourcePaths = foundFiles.stream()
            .map(filePath -> ResourcePath.from(resourceDirectory, filePath))
            .collect(Collectors.toUnmodifiableList());

        return resourcePaths.stream()
            .map(resourcePath -> new StaticResourceEndPointMatcher(staticResourceFindMethod, resourcePath.getResourceUrl(), resourcePath.getResourcePath(), PATH_VARIABLE_KEY))
            .collect(Collectors.toUnmodifiableList());
    }

    private static class ResourcePath {
        private static final String DIRECTORY_DELIMITER = "/";

        private final Path resourcePath;
        private final PathUrl resourceUrl;

        public ResourcePath(Path resourcePath, PathUrl resourceUrl) {
            Objects.requireNonNull(resourcePath);
            Objects.requireNonNull(resourceUrl);
            this.resourcePath = resourcePath;
            this.resourceUrl = resourceUrl;
        }

        public static ResourcePath from(Path resourceDirectory, Path resourcePath) {
            Path packageResourcePath = resourceDirectory.relativize(resourcePath);
            Path resourceUrl = Path.of(DIRECTORY_DELIMITER).resolve(packageResourcePath);
            return new ResourcePath(resourcePath, PathUrl.from(resourceUrl));
        }

        public Path getResourcePath() {
            return resourcePath;
        }

        public PathUrl getResourceUrl() {
            return resourceUrl;
        }
    }

    private static List<Path> findFilePath(Path resourceDirectory) {
        try (Stream<Path> fileWalk = Files.walk(resourceDirectory)) {
            return fileWalk
                .filter(filePath -> !Files.isDirectory(filePath))
                .peek(filePath -> log.info("filePath : {}", filePath))
                .collect(Collectors.toUnmodifiableList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Method getStaticResourceFindMethod() {
        try {
            return StaticResourceFinder.class.getMethod("find", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static class StaticResourceFinder {
        public static Path find(@PathVariable(PATH_VARIABLE_KEY) String resourceUrl) {
            Path resourcePath = Path.of(resourceUrl);
            log.info("resourcePath : `{}`", resourcePath);

            if (Files.notExists(resourcePath)) {
                log.info("file does not exist");
                throw new RuntimeException("path does not exist");
            }
            return resourcePath;
        }
    }
}
