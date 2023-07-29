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
public class PackageResourceFinder {
    private static final String DIRECTORY_DELIMITER = "/";

    private final Path resourceDirectory;
    private final ResourceUrls resourceUrls;

    private PackageResourceFinder(Path resourceDirectory) {
        Objects.requireNonNull(resourceDirectory);
        log.info("resourceDirectory : {}", resourceDirectory);
        this.resourceDirectory = resourceDirectory.normalize();
        this.resourceUrls = extractResourceUrls(this.resourceDirectory);
    }

    public static PackageResourceFinder from(Class<?> clazz, String resourcePackage) {
        Objects.requireNonNull(clazz);
        if (Objects.isNull(resourcePackage) || resourcePackage.isBlank()) {
            throw new RuntimeException("requestPackage is empty.");
        }

        Path clazzPath = FileSystemUtil.getClazzRootPath(clazz);
        Path projectPackageDirectory = clazzPath.getParent();
        Path resourceDirectory = projectPackageDirectory.resolve(resourcePackage);
        resourceDirectory = resourceDirectory.normalize();

        return new PackageResourceFinder(resourceDirectory);
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

//    1. resource 를 찾기 위한 path 를 받는다.
//    2. findUrl 을 일반화한다.
//    3. 등록된 url 에 findUrl 이 존재하는 지 확인한다.
//    4. resource directory path, find url path 를 조합하여 resource path 를 생성한다.
    public Optional<Path> findResource(Path findUrl) {
        if (Objects.isNull(findUrl)) {
            return Optional.empty();
        }
        findUrl = findUrl.normalize();

        boolean doesNotExistMatchUrl = !resourceUrls.contain(findUrl);
        if (doesNotExistMatchUrl) {
            log.info("does not exist MatchUrl.");
            return Optional.empty();
        }

        if (findUrl.startsWith(DIRECTORY_DELIMITER)) {
            findUrl = Path.of(findUrl.toString().substring(1));
        }

        Path canonicalResourcePath = resourceDirectory.resolve(findUrl);

        if (Files.notExists(canonicalResourcePath)) {
            log.info("file does not exist");
            return Optional.empty();
        }
        return Optional.of(canonicalResourcePath);
    }
}
