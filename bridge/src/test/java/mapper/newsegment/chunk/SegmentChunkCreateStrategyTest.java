package mapper.newsegment.chunk;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SegmentChunkCreateStrategyTest {

    @DisplayName("path 가 루트만 존재하면 emptySegmentChunk 를 생성합니다.")
    @Test
    void test0() throws Exception {
        //given
        String rootPath = "/";
        Queue<SegmentChunk> expect = new ArrayDeque<>(List.of(new EmptySegmentChunk()));

        //when
        Queue<SegmentChunk> actual = SegmentChunkCreateStrategy.create(rootPath);

        //then
        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual.size()).isEqualTo(expect.size());

        Iterator<SegmentChunk> actualIter = actual.iterator();
        Iterator<SegmentChunk> expectIter = expect.iterator();
        while (actualIter.hasNext()) {
            SegmentChunk actualChunk = actualIter.next();
            SegmentChunk expectChunk = expectIter.next();

            Assertions.assertThat(actualChunk.getClass()).isEqualTo(expectChunk.getClass());
        }
    }

    @DisplayName("normal path 만 존재하면 NormalSegmentChunk 를 생성합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/normalPath",
        "normalPath",
        "/normalPath/p2",
        "normalPath/p2",
        "/normalPath/p2/p3",
        "normalPath/p2/p3"
    })
    void test1(String path) throws Exception {
        //given
        Class<?> expectClazz = NormalSegmentChunk.class;

        //when
        Queue<SegmentChunk> actualQueue = SegmentChunkCreateStrategy.create(path);

        //then
        Assertions.assertThat(actualQueue.size()).isEqualTo(1);
        SegmentChunk actualChunk = actualQueue.poll();
        Assertions.assertThat(actualChunk).isInstanceOf(expectClazz);
    }

    @DisplayName("pathVariable 을 포함한 path 이면 PathVariableSegmentChunk 를 생성합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/normalPath/{pathVariable}",
        "/normalPath/{pathVariable}",
        "/{pathVariable}",
        "{pathVariable}",
        "/{pathVariable}/p1",
        "{pathVariable}/p1",
        "/{pathVariable}/{pv2}",
        "{pathVariable}/{pv2}",
    })
    void test2(String path) throws Exception {
        //given
        Class<?> expectClazz = PathVariableSegmentChunk.class;

        //when
        Queue<SegmentChunk> actualQueue = SegmentChunkCreateStrategy.create(path);

        //then
        Assertions.assertThat(actualQueue.size()).isEqualTo(1);
        SegmentChunk actualChunk = actualQueue.poll();
        Assertions.assertThat(actualChunk).isInstanceOf(expectClazz);
    }

    @DisplayName("wildCard 를 포함한 path 이면 WildCardSegmentChunk 를 생성합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/**",
        "**",
        "/**/normalPath",
        "**/normalPath",
        "/**/{pathVariable}",
        "**/{pathVariable}",
        "/**/normalPath/{pathVariable}",
        "**/normalPath/{pathVariable}",
        "/**/normalPath/{pathVariable}/normalPath1",
        "**/normalPath/{pathVariable}/normalPath1",
        "/**/normalPath/{pathVariable}/normalPath1/p2",
        "**/normalPath/{pathVariable}/normalPath1/p2",
        "/**/{pathVariable}/normalPath/",
        "**/{pathVariable}/normalPath/",
        "/**/{pathVariable}/normalPath/p2",
        "**/{pathVariable}/normalPath/p2",
        "/**/{pathVariable}/normalPath/{pv2}",
        "**/{pathVariable}/normalPath/{pv2}",
        "/**/{pathVariable}/normalPath/{pv2}",
    })
    void test(String path) throws Exception {
        //given
        Class<?> expectClazz = WildCardSegmentChunk.class;

        //when
        Queue<SegmentChunk> actualQueue = SegmentChunkCreateStrategy.create(path);

        //then
        Assertions.assertThat(actualQueue.size()).isEqualTo(1);
        SegmentChunk actualChunk = actualQueue.poll();
        Assertions.assertThat(actualChunk).isInstanceOf(expectClazz);
    }

    @DisplayName("wildCard 를 기준으로 path 를 n 개로 chunk 로 나누고, 나누어진 chunk 각각을 SegmentChunk 로 생성합니다.")
    @ParameterizedTest
    @MethodSource("provideSinglePathAndExpectClasses")
    void test4(String path, Queue<Class<?>> expect) throws Exception {
        //given
        //when
        Queue<SegmentChunk> actual = SegmentChunkCreateStrategy.create(path);

        //then
        Assertions.assertThat(actual.size()).isEqualTo(expect.size());

        Iterator<SegmentChunk> actualIter = actual.iterator();
        Iterator<Class<?>> expectIter = expect.iterator();

        while (actualIter.hasNext()) {
            SegmentChunk actualChunk = actualIter.next();
            Class<?> expectClass = expectIter.next();

            Assertions.assertThat(actualChunk).isInstanceOf(expectClass);
        }
    }

    public static Stream<Arguments> provideSinglePathAndExpectClasses() {
        return Stream.of(
            Arguments.of("/normalPath/**", new ArrayDeque<>(List.of(NormalSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("normalPath/**", new ArrayDeque<>(List.of(NormalSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/{pathVariable}/**", new ArrayDeque<>(List.of(PathVariableSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("{pathVariable}/**", new ArrayDeque<>(List.of(PathVariableSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/normalPath/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/normalPath/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/{pathVariable}/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/{pathVariable}/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/normalPath/{pathVariable}/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/normalPath/{pathVariable}/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/normalPath/{pathVariable}/normalPath1/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/normalPath/{pathVariable}/normalPath1/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/normalPath/{pathVariable}/normalPath1/p2/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/normalPath/{pathVariable}/normalPath1/p2/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/{pathVariable}/normalPath/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/{pathVariable}/normalPath/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/{pathVariable}/normalPath/p2/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/{pathVariable}/normalPath/p2/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/{pathVariable}/normalPath/{pv2}/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("**/{pathVariable}/normalPath/{pv2}/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/{pathVariable}/normalPath/{pv2}/**", new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class))),
            Arguments.of("/**/{pathVariable}/normalPath/{pv2}/**/p1/p2/{pv2}/**/p3/**",
                         new ArrayDeque<>(List.of(WildCardSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class, WildCardSegmentChunk.class)))
        );

    }
}