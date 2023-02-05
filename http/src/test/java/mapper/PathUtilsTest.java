package mapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathUtilsTest {

    @DisplayName("파일 시스템의 path 로 부터, class 파일을 생성합니다.")
    @Test
    void test0_0() throws Exception {
        //given
        Path root = getRootPath();
        Path classFile = getPathUtilsTestClassPath();

        //when
        Throwable actual = Assertions.catchThrowable(() -> PathUtils.createClass(root, classFile));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("파일 시스템의 path 에 파일이 존재하지 않으면, exception 이 발생합니다.")
    @Test
    void test0_1() throws Exception {
        //given
        Path root = getRootPath();
        Path doesNotExistClassFile = getDoesNotExistClassFile();

        //when
        Throwable actual = Assertions.catchThrowable(() -> PathUtils.createClass(root, doesNotExistClassFile));

        //then
        Assertions.assertThat(actual).isNull();
    }
    private static Path getPathUtilsTestClassPath() throws URISyntaxException {
        URI clazzURI = PathUtilsTest.class.getResource("PathUtilsTest.class").toURI();
        Path path = Paths.get(clazzURI);
        return path;
    }

    private static Path getDoesNotExistClassFile(){
        return Paths.get("DoesNotExistClass.class");
    }

    private static Path getRootPath() throws URISyntaxException, IOException {
        URI rootURI = PathUtilsTest.class.getResource("").toURI();
        if (rootURI.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(rootURI, Collections.emptyMap());
            return fileSystem.getPath("");
        }

        rootURI = PathUtilsTest.class.getResource("/").toURI();
        return Paths.get(rootURI);
    }


}