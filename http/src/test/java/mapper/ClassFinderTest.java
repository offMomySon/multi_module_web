package mapper;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClassFinderTest {

    @DisplayName("")
    @Test
    void test() throws Exception {
//        //given
//        ClassLoader classLoader = ClassFinder.class.getClassLoader();
//        Path path = Paths.get(classLoader.getResource("").toURI());
//        ClassFinder classFinder = new ClassFinder();
//
//        //when
//        classFinder.doLogAllRegularFile(path);
//
//        //then

    }

    @DisplayName("")
    @Test
    void test2() throws Exception {
        //given
        Path path = Path.of("/Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main/com/main/controller/SampleController.class");
        Path relativePath = Path.of("/Users/huni1006/Personal_Project/multi_module_web/build/classes/java/main");

        Path relativize = relativePath.relativize(path);
        System.out.println("relativize : " + relativize);

        Path fileName = path.getFileName();
        System.out.println("fileName : " + fileName);

        boolean isEndWithClassExtension = fileName.toString().endsWith(".class");
        System.out.println("isEndWithClassExtension : " + isEndWithClassExtension);

        //when

        //then

    }

}