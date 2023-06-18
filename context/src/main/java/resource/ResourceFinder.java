package resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import util.FileSystemUtil;

@Slf4j
public class ResourceFinder {
    private final Path resourceDirectory;

    public ResourceFinder(Path resourceDirectory) {
        Objects.requireNonNull(resourceDirectory);
        log.info("resourceDirectory : {}", resourceDirectory);
        this.resourceDirectory = resourceDirectory;
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

    public List<String> findResourceUrls() {
        try (Stream<Path> fileWalk = Files.walk(resourceDirectory)) {
            return fileWalk
                .filter(path -> !Files.isDirectory(path))
                .map(Path::toString)
                .peek(url -> log.info("url : {}", url))
                .collect(Collectors.toUnmodifiableList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Path> findResource(Path resourceUrl) {
        Objects.requireNonNull(resourceUrl);

        resourceUrl = resourceUrl.normalize();
        String resourceFile = resourceUrl.toString().substring(1);

        Path fullPathResourceFile = resourceDirectory.resolve(resourceFile);

        log.info("relativeResourceFile : {}", resourceFile);
        log.info("fullPathResourceFile : {}", fullPathResourceFile);

        if (Files.notExists(fullPathResourceFile)) {
            log.info("file does not exist");
            return Optional.empty();
        }
        return Optional.of(fullPathResourceFile);
    }
}
