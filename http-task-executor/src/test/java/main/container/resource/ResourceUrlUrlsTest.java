package main.container.resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Set;

class ResourceUrlUrlsTest {

    @DisplayName("")
    @Test
    void test() throws Exception {
        //given
        Path of = Path.of("/result1.txt");
        Path of2 = Path.of("/result1.txt");

        Set<Path> some = Set.of(of);
        boolean contains = some.contains(of2);
        System.out.println(contains);
        //when

        //then

    }
}