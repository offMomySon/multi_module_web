package mapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

//    TODO
//    * classLoader 로 파일시스템에 접근시, jar 과 intellij 에서의 특징.
//      * fat jar 시스템
//         * classLoader 를 통해 jar 시스템 루트에 접근할 수 없다. 반드시 지정한 디렉토리가 있어야 한다.
//              ex.
//                  App.class.getClassLoader().getResource("/");
//                  Files.walk(rootPath).forEach(path -> log.info("path : {}", path));
//                  -> null
//                  App.class.getClassLoader().getResource("/com");
//                  Files.walk(rootPath).forEach(path -> log.info("path : {}", path));
//                  ->
//      * intellij 시스템
//          * classLoader 에서 반환되는 url 은 파일시스템 scheme 을 가진 url 을 가지고 하위 파일들을 찾기 때문에,
//          모듈들로 부터 생성된 서로다른 build 디렉토리 중 하나의 class 에서 생성된 Path 가 다른 build 디렉토리의 class 를 찾기 못한다.
//          ( class path 로 파일들을 찾지 않기 때문에..?)
//          예를 들자면, A build directory 에 존재하는 A.class 로 부터 생성된 Path 로 부터, B build directory 에 존재하는 B.class 파일 찾지 못한다.
//
//          * 왜 root path 를 지정하면 아래 path 를 지칭하는가?
//          /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main
//              ex.
//                  App.getClassLoader().getResource("").toURI();
//                  Files.walk(rootPath).forEach(path -> log.info("path : {}", path));
//->
//09:00:15.156 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main
//09:00:15.158 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com
//09:00:15.158 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main
//09:00:15.158 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main/controller
//09:00:15.158 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main/controller/SampleController.class
//09:00:15.158 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main/controller/testPack
//09:00:15.158 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main/controller/testPack/TestClass.class
//09:00:15.159 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main/controller/ResponseDate.class
//09:00:15.159 [main] INFO mapper.ClassFinder - path : /Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main/App.class
//
//    ClassLoaderA

/**
 * 루틴클래스.
 *
 * 역할.
 * 파일 시스템에서 특정 package 하위에 존재하는 class 파일들을 찾는 역할.
 *
 * 루틴.
 * 1. 특정 path 의 하위의 모든 파일을 찾는다.
 * 2. 일반파일 여부를 확인한다.
 * 3. .class 확장자를 가지고 있는지 확인한다.
 * 4. root path 와 file path 를 가지고 class 를 생성한다.
 */
@Slf4j
public class FileSystemClassFinder {
    private final Path rootPath;
    private final Path findPath;

    private FileSystemClassFinder(Path rootPath, Path findPath) {
        validateEmpty(rootPath);
        validateEmpty(findPath);

        this.rootPath = rootPath;
        this.findPath = findPath;
    }

    public static FileSystemClassFinder from(Class<?> _clazz, String findPackage) {
        validateEmpty(_clazz);
        validateEmpty(findPackage);

        try {
            Path rootPath = getRootPath(_clazz);
            Path findPath = rootPath.resolve(findPackage.replace(".", "/"));
            log.info("rootPath : {}", rootPath);
            log.info("findPath : {}", findPath);

            return new FileSystemClassFinder(rootPath, findPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(MessageFormat.format("uri syntax exception. {}", e.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("io exception. {}", e.getMessage()));
        }
    }

    public List<? extends Class<?>> find() {
        try (Stream<Path> walk = Files.walk(this.findPath)) {
            List<? extends Class<?>> foundClazzes = walk
                .filter(Files::isRegularFile)
                .filter(FileSystemClassFinder::hasClassExtension)
                .map(filePath -> PathUtils.createClass(rootPath, filePath))
                .collect(Collectors.toUnmodifiableList());
            return foundClazzes;
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("io exception. {}", e.getMessage()));
        }
    }

    private static boolean hasClassExtension(Path path) {
        return path.getFileName().toString().endsWith(".class");
    }

    private static Path getRootPath(Class<?> clazz) throws IOException, URISyntaxException {
        URI uri = clazz.getResource("").toURI();
        if (isJar(uri)) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            return fileSystem.getPath("");
        }

        uri = clazz.getResource("/").toURI();
        return Paths.get(uri);
    }

    private static boolean isJar(URI uri) {
        return uri.getScheme().equals("jar");
    }

    private static <T> T validateEmpty(T value) {
        if (Objects.isNull(value)) {
            throw new RuntimeException(MessageFormat.format("value is null. `type`/`value` -> `{0}/`{1}`", value.getClass().getSimpleName(), value));
        }
        if (Objects.equals(value.getClass(), String.class) &&
            (((String) value).isEmpty() || ((String) value).isBlank())) {
            throw new RuntimeException(MessageFormat.format("value is empty. `type`/`value` -> `{0}`/`{1}`", value.getClass().getSimpleName(), value));
        }

        return value;
    }
}
