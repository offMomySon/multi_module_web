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

//    request url 을 받아온다.
//    request url 을 일반화 한다.
//    등록된 resourceUrls 중에서 request url 와 동일한 url 이 존재하는지 확인한다.
//    requestUrl 에서 / 를 제거한다.
//    resource directory 와 request url 을 합쳐 resource 위치를 가리키는 resource Path 를 생성한다.
//    resource Path 를 반환한다.
    public Optional<Path> find(Path findUrl) {
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
