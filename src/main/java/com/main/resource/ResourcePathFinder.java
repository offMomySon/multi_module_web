package com.main.resource;

import com.main.util.FileSystemUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import matcher.StaticResourceEndPointTaskMatcher;
import matcher.segment.PathUrl2;

@Slf4j
public class ResourcePathFinder {
    private final Path resourceDirectory;
    private final String urlPrefix;

    private ResourcePathFinder(Path resourceDirectory, String urlPrefix) {
        Objects.requireNonNull(resourceDirectory);
        Objects.requireNonNull(urlPrefix);
        this.resourceDirectory = resourceDirectory.normalize();
        this.urlPrefix = urlPrefix;
    }

    public static ResourcePathFinder from(Class<?> clazz, String resourcePackage, String urlPrefix) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(urlPrefix);
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzPath = FileSystemUtil.getClazzRootPath(clazz);
        Path projectPackageDirectory = clazzPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage).normalize();
        log.info("clazzPath : {}", clazzPath);
        log.info("projectPackageDirectory : {}", projectPackageDirectory);
        log.info("resourceDirectory : {}", resourceDirectory);
        return new ResourcePathFinder(resourceDirectory, urlPrefix);
    }

    public List<Path> find() {
        return findFilePath(resourceDirectory);
    }

    public List<StaticResourceEndPointTaskMatcher> create() {
        List<Path> foundResources = findFilePath(resourceDirectory);
        List<ResourceUrl> resourceUrls = foundResources.stream()
            .map(foundResource -> ResourceUrl.from(resourceDirectory, foundResource, urlPrefix))
            .collect(Collectors.toUnmodifiableList());

        return resourceUrls.stream()
            .map(ResourcePathFinder::createStaticResourceEndPointMatcher)
            .peek(endPointTaskMatcher -> log.info("EndpointTaskMatcher : `{}`", endPointTaskMatcher))
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<Path> findFilePath(Path resourceDirectory) {
        try (Stream<Path> fileWalk = Files.walk(resourceDirectory)) {
            return fileWalk
                .filter(filePath -> !Files.isDirectory(filePath))
                .collect(Collectors.toUnmodifiableList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static StaticResourceEndPointTaskMatcher createStaticResourceEndPointMatcher(ResourceUrl resourceAndUrl) {
        PathUrl2 resourceUrl = resourceAndUrl.getUrl();
        Path resource = resourceAndUrl.getResource();
        return new StaticResourceEndPointTaskMatcher(resourceUrl, resource);
    }

    private static class ResourceUrl {
        private static final String DELIMITER = "/";

        private final Path resource;
        private final PathUrl2 url;

        public ResourceUrl(Path resource, PathUrl2 url) {
            Objects.requireNonNull(resource);
            Objects.requireNonNull(url);
            this.resource = resource;
            this.url = url;
        }

        public static ResourceUrl from(Path resourceDirectory, Path resource, String urlPrefix) {
            Objects.requireNonNull(resourceDirectory);
            Objects.requireNonNull(resource);
            Objects.requireNonNull(urlPrefix);

            Path relativeResourcePath = resourceDirectory.relativize(resource);
            Path resourcePath = Path.of(DELIMITER).resolve(urlPrefix).resolve(relativeResourcePath);
            PathUrl2 resourceUrl = PathUrl2.from(resourcePath);
            return new ResourceUrl(resource, resourceUrl);
        }

        public Path getResource() {
            return resource;
        }

        public PathUrl2 getUrl() {
            return url;
        }
    }
}
