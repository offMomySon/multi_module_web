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
 * 역할.
 * 파일 시스템의 path 하위의 class 들을 찾는 역할.
 */
@Slf4j
public class FileSystemClassFinder {
    private final Path rootPath;
    private final Path classFindPath;

    private FileSystemClassFinder(Path rootPath, Path classSerachPath) {
        validateEmpty(rootPath);
        validateEmpty(classSerachPath);

        this.rootPath = rootPath;
        this.classFindPath = classSerachPath;
    }

    public static FileSystemClassFinder from(Class<?> _clazz, String classSearchPackage) {
        validateEmpty(_clazz);
        validateEmpty(classSearchPackage);

        try {
            Path systemRootPath = getRootPath(_clazz);
            Path classFindPath = systemRootPath.resolve(classSearchPackage.replace(".", "/"));
            log.info("systemRootPath : {}", systemRootPath);
            log.info("classFindPath : {}", classFindPath);

            return new FileSystemClassFinder(systemRootPath, classFindPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(MessageFormat.format("uri syntax exception. {}", e.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("io exception. {}", e.getMessage()));
        }
    }

    public List<? extends Class<?>> find() {
        try (Stream<Path> walk = Files.walk(this.classFindPath)) {
            List<? extends Class<?>> foundClazzes = walk
                .filter(Files::isRegularFile)
                .peek(regularFile -> log.info("[1] regularFile : {}", regularFile))
                .filter(FileSystemClassFinder::hasClassExtension)
                .peek(classFile -> log.info("[2] classFile : {}", classFile))
                .map(rootPath::relativize)
                .peek(packagePath -> log.info("[3] packagePath : {}", packagePath))
                .map(FileSystemClassFinder::convertFullyQualifiedClassName)
                .peek(className -> log.info("[4] className : {}", className))
                .map(FileSystemClassFinder::createClass)
                .collect(Collectors.toUnmodifiableList());

            return foundClazzes;
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("io exception. {}", e.getMessage()));
        }
    }

    private static Class<?> createClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertFullyQualifiedClassName(Path packagePath) {
        return packagePath.toString()
            .substring(0, packagePath.toString().lastIndexOf(".class"))
            .replace("/", ".");
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
            (((String)value).isEmpty() || ((String)value).isBlank())) {
            throw new RuntimeException(MessageFormat.format("value is empty. `type`/`value` -> `{0}`/`{1}`", value.getClass().getSimpleName(), value));
        }

        return value;
    }
}
