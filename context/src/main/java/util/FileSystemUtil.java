package util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;


/**
 * 루틴클래스.
 * 역할.
 * 파일 시스템에서 특정 package 하위에 존재하는 class 파일들을 찾는 역할.
 * 루틴.
 * 1. 특정 path 의 하위의 모든 파일을 찾는다.
 * 2. 일반파일 여부를 확인한다.
 * 3. .class 확장자를 가지고 있는지 확인한다.
 * 4. root path 와 file path 를 가지고 class 를 생성한다.
 */
@Slf4j
public class FileSystemUtil {
    /**
     * 인자로 받아온 패키지 하위의 모든 클래스를 가져온다.
     * bootClass 파일시스템의 루트를 지정하기 위해 사용된다.
     * findPackage 는 class 검색의 시작점을 지정하기 위해 사용된다.
     */
    public static List<Class<?>> findClass(Class<?> bootClass, String findPackage) {
        if (Objects.isNull(bootClass) || Objects.isNull(findPackage)) {
            throw new RuntimeException("parameter is null.");
        }

        try {
            Path rootPath = getRoot(bootClass);
            Path findPath = rootPath.resolve(findPackage.replace(".", "/"));
            log.info("findPath : {}", findPath);

            try (Stream<Path> pathStream = Files.walk(findPath)) {
                List<Path> classFilePaths = pathStream
                    .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(FileSystemUtil::hasClassExtension)
                    .collect(Collectors.toUnmodifiableList());

                return classFilePaths.stream()
                    .map(classFilePath -> generateFullyQualifiedClassName(rootPath, classFilePath))
                    .map(FileSystemUtil::getClass)
                    .collect(Collectors.toUnmodifiableList());
            }
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("io exception. {}", e.getMessage()));
        }
    }

    public static Path getRoot(Class<?> bootClazz) {
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

        return "jar".equalsIgnoreCase(scheme);
    }

    private static boolean hasClassExtension(Path fileName) {
        return fileName.getFileName().toString().endsWith(".class");
    }

    private static String generateFullyQualifiedClassName(Path rootPath, Path classFilePath) {
        Path jvmPath = rootPath.relativize(classFilePath);

        return jvmPath.toString()
            .substring(0, jvmPath.toString().lastIndexOf(".class"))
            .replace("/", ".");
    }

    private static Class<?> getClass(String n) {
        try {
            return Class.forName(n);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
