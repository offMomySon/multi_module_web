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

    private final Path resourceDirectory;
    private final String urlPrefix;

    private StaticResourceEndPointCreator(Path resourceDirectory, String urlPrefix) {
        Objects.requireNonNull(resourceDirectory);
        Objects.requireNonNull(urlPrefix);
        log.info("resourceDirectory : {}", resourceDirectory);
        log.info("urlPrefix : {}", urlPrefix);
        this.resourceDirectory = resourceDirectory.normalize();
        this.urlPrefix = urlPrefix;
    }

    public static StaticResourceEndPointCreator from(Class<?> clazz, String resourcePackage, String urlPrefix) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(urlPrefix);
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzPath = FileSystemUtil.getClazzRootPath(clazz);
        Path projectPackageDirectory = clazzPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage);
        resourceDirectory = resourceDirectory.normalize();
        return new StaticResourceEndPointCreator(resourceDirectory, urlPrefix);
    }

    public List<StaticResourceEndPointMatcher> create() {
        List<Path> foundResources = findFilePath(resourceDirectory);
        List<ResourceUrl> resourceUrls = foundResources.stream()
            .map(foundResource -> ResourceUrl.from(resourceDirectory, foundResource, urlPrefix))
            .collect(Collectors.toUnmodifiableList());

        return resourceUrls.stream()
            .map(StaticResourceEndPointCreator::createStaticResourceEndPointMatcher)
            .collect(Collectors.toUnmodifiableList());
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

    private static StaticResourceEndPointMatcher createStaticResourceEndPointMatcher(ResourceUrl resourceAndUrl) {
        PathUrl resourceUrl = resourceAndUrl.getUrl();
        Path resource = resourceAndUrl.getResource();
        return new StaticResourceEndPointMatcher(resourceUrl, resource);
    }

    private static class ResourceUrl {
        private static final String DELIMITER = "/";

        private final Path resource;
        private final PathUrl url;

        public ResourceUrl(Path resource, PathUrl url) {
            Objects.requireNonNull(resource);
            Objects.requireNonNull(url);
            this.resource = resource;
            this.url = url;
        }

        public static ResourceUrl from(Path resourceDirectory, Path resource, String urlPrefix){
            Objects.requireNonNull(resourceDirectory);
            Objects.requireNonNull(resource);
            Objects.requireNonNull(urlPrefix);

            Path relativeResourcePath = resourceDirectory.relativize(resource);
            Path resourcePath = Path.of(DELIMITER).resolve(urlPrefix).resolve(DELIMITER).resolve(relativeResourcePath);
            PathUrl resourceUrl = PathUrl.from(resourcePath);
            return new ResourceUrl(resource, resourceUrl);
        }

        public Path getResource() {
            return resource;
        }

        public PathUrl getUrl() {
            return url;
        }
    }
}
