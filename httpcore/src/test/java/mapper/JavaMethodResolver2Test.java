package mapper;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vo.HttpMethod;

class JavaMethodResolver2Test {

    @DisplayName("")
    @ParameterizedTest
    @MethodSource("provideMatchTestCase")
    void test(String path, String[] testPaths) throws Exception {
        //given
        JavaMethodResolver2 resolver = new JavaMethodResolver2(HttpMethod.GET, path, JavaMethodResolver2.class.getMethod("match", String.class));

        //when
        List<Boolean> actuals = Arrays.stream(testPaths)
            .map(resolver::match)
            .collect(Collectors.toUnmodifiableList());

        //then
        Assertions.assertThat(actuals)
            .allSatisfy(actual ->
                            Assertions.assertThat(actual)
                                .isTrue());
    }


    public static Stream<Arguments> provideMatchTestCase() {
        return Stream.of(
            Arguments.of(
                "/test/path",
                         new String[]{"/test/path"
            }),
            Arguments.of(
                "/test/**",
                         new String[]{
                             "/test/1",
                             "/test/1/2",
                             "/test/1/2/3"
            }),
            Arguments.of(
                "/test/**/wardcard",
                         new String[]{
                             "/test/1/wardcard",
                             "/test/1/2/wardcard",
                             "/test/1/2/3/wardcard"
            }),
            Arguments.of(
                "/test/**/path/**",
                         new String[]{
                             "/test/1/path/1",
                             "/test/1/2/path/1",
                             "/test/1/2/3/path/1",
                             "/test/1/2/3/4/path/1",
                             "/test/1/path/1/2",
                             "/test/1/path/1/2/3",
                             "/test/1/path/1/2/3/4",
                             "/test/1/2/path/1/2",
                             "/test/1/2/3/path/1/2/3"
            })
        );
    }
}