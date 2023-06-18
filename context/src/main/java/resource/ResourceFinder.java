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
        this.resourceDirectory = resourceDirectory;
    }

    public static ResourceFinder create(Class<?> bootClazz, String resourcePackage) {
        if (Objects.isNull(bootClazz)) {
            throw new RuntimeException("parameter is null.");
        }
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzRoot = FileSystemUtil.getRoot(bootClazz);
        log.info("clazzRoot : {}", clazzRoot);
        Path projectPackage = clazzRoot.getParent();
        log.info("projectPackage : {}", projectPackage);
        Path resourceDirectory = projectPackage.resolve(resourcePackage);
        log.info("resourceDirectory : {}", resourceDirectory);

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

    public Optional<Path> findResource(Path resourceFile) {
        if (Objects.isNull(resourceFile)) {
            throw new RuntimeException("resource is null.");
        }
        resourceFile = resourceFile.normalize();
        String relativeResourceFile = resourceFile.toString().substring(1);
        log.info("relativeResourceFile : {}", relativeResourceFile);

        Path fullPathResourceFile = resourceDirectory.resolve(relativeResourceFile);
        log.info("fullPathResourceFile : {}", fullPathResourceFile);

        if (Files.notExists(fullPathResourceFile)) {
            log.info("file does not exist");
            return Optional.empty();
        }
        return Optional.of(fullPathResourceFile);
    }
}
