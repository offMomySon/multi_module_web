package read;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileFinder {
    private final Path resourceDirectory;

    public FileFinder(Path resourceDirectory) {
        Objects.requireNonNull(resourceDirectory);
        this.resourceDirectory = resourceDirectory;
    }

    public static FileFinder create(Class<?> bootClazz, String resourcePackage) {
        if (Objects.isNull(bootClazz)) {
            throw new RuntimeException("parameter is null.");
        }
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzRoot = getRootPath(bootClazz);
        log.info("clazzRoot : {}", clazzRoot);
        Path projectPackage = clazzRoot.getParent();
        log.info("projectPackage : {}", projectPackage);
        Path resourceDirectory = projectPackage.resolve(resourcePackage);
        log.info("resourceDirectory : {}", resourceDirectory);

        return new FileFinder(resourceDirectory);
    }

    private static Path getRootPath(Class<?> bootClazz) {
        try {
            URL classDirectoryUrl = bootClazz.getResource("");
            if (Objects.isNull(classDirectoryUrl)) {
                throw new RuntimeException("classDirectoryUrl is null.");
            }
            URI classDirectoryUri = classDirectoryUrl.toURI();
            log.info("classDirectoryUri : {}", classDirectoryUri);

            if (isJarFileSystem(classDirectoryUri)) {
                try (FileSystem jarFileSystem = FileSystems.newFileSystem(classDirectoryUri, Collections.emptyMap())) {
                    Path rootPath = jarFileSystem.getPath("/");
                    log.info("rootPath : {}", rootPath);
                    return rootPath;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            URL rootUrl = bootClazz.getResource("/");
            if (Objects.isNull(rootUrl)) {
                throw new RuntimeException("uri is null.");
            }
            URI rootUri = rootUrl.toURI();
            log.info("rootUri : {}", rootUri);
            return Paths.get(rootUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isJarFileSystem(URI uri) {
        String scheme = uri.getScheme();
        log.info("scheme : {}", scheme);
        return "jar".equals(scheme);
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
