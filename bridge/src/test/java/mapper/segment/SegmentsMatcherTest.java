package mapper.segment;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mapper.segment.SegmentsMatcher.MatchResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import vo.RequestValues;

class SegmentsMatcherTest {

    @DisplayName("WILD CARD 패턴은 0 번 혹은 첫번쨰 path segment 에 한번 들어가면 생성할 수 있습니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/",
        "/path1",
        "/path1/path2",
        "/path1/path2/path3",
        "/**",
        "/**/path1",
        "/**/path1/path2",
    })
    void test(String path) throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new SegmentsMatcher(path));

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("WILD CARD 패턴이 첫번째 path segment 에 존재하지 않으면 excpeiton 이 발생합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/path1/**",
        "/path1/**/path2",
        "/path1/path2/**",
        "/path1/**/path2/path3",
        "/path1/path2/**/path3",
        "/path1/path2/path3/**"
    })
    void test1(String path) throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new SegmentsMatcher(path));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("WILD CARD 패턴이 2번이상 존재하면 exception 이 발생합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/**/**",
        "/**/path1/**",
        "/**/**/path1",
        "/**/path1/path2/**",
        "/**/path1/**/path2",
        "/**/**/path1/path2",
        "/**/path1/path2/path3/**",
        "/**/path1/path2/**/path3",
        "/**/path1/**/path2/path3",
        "/**/**/path1/path2/path3",
    })
    void test2(String path) throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> new SegmentsMatcher(path));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("match 가능하면, 남은 path 를 가져옵니다.")
    @ParameterizedTest
    @MethodSource("providePathAndExpectPaths")
    void test3(String methodPath, String requestPath, Set<String> expect) throws Exception {
        //given
        SegmentsMatcher matcher = new SegmentsMatcher(methodPath);

        //when
        List<MatchResult> match = matcher.match(requestPath);
        Set<String> actual = match.stream()
            .map(MatchResult::getLeftPath)
            .collect(Collectors.toUnmodifiableSet());

        //then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    public static Stream<Arguments> providePathAndExpectPaths() {
        return Stream.of(
            Arguments.of("/path1", "/path1", Set.of("")),
            Arguments.of("/path1", "/path1/path2", Set.of("/path2")),
            Arguments.of("/path1", "/path1/path2/path3", Set.of("/path2/path3")),
            Arguments.of("/path1/path2", "/path1/path2/path3", Set.of("/path3")),
            Arguments.of("/**", "/", Set.of("")),
            Arguments.of("/**", "/path1", Set.of("", "/path1")),
            Arguments.of("/**", "/path1/path2", Set.of("", "/path2", "/path1/path2")),
            Arguments.of("/**", "/path1/path2/path3", Set.of("", "/path3", "/path2/path3", "/path1/path2/path3")),
            Arguments.of("/**/path1", "/path1", Set.of("")),
            Arguments.of("/**/path1", "/path1/path2", Set.of("/path2")),
            Arguments.of("/**/path1", "/path1/path2/path3", Set.of("/path2/path3")),
            Arguments.of("/**/path2", "/path1/path2", Set.of("")),
            Arguments.of("/**/path2", "/path1/path2/path3", Set.of("/path3")),
            Arguments.of("/**/path2", "/path1/path2/path3/path4", Set.of("/path3/path4")),
            Arguments.of("/**/path3", "/path1/path2/path3/path4", Set.of("/path4")),
            Arguments.of("/**/path2/path3", "/path1/path2/path3", Set.of("")),
            Arguments.of("/**/path2/path3", "/path1/path2/path3/path4", Set.of("/path4")),
            Arguments.of("/{pv1}", "/pv1", Set.of("")),
            Arguments.of("/{pv1}", "/pv1/pv2", Set.of("/pv2")),
            Arguments.of("/{pv1}", "/pv1/pv2/pv3", Set.of("/pv2/pv3")),
            Arguments.of("/{pv1}/{pv2}", "/pv1/pv2", Set.of("")),
            Arguments.of("/{pv1}/{pv2}", "/pv1/pv2/pv3", Set.of("/pv3")),
            Arguments.of("/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3", Set.of("")),
            Arguments.of("/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3/pv4", Set.of("/pv4")),
            Arguments.of("/**/{pv1}", "/pv1", Set.of("")),
            Arguments.of("/**/{pv1}/path1", "/pv1/path1", Set.of("")),
            Arguments.of("/**/{pv1}/path1", "/pv1/path1/path2", Set.of("/path2")),
            Arguments.of("/**/path1/{pv1}", "/path1/pv1", Set.of("")),
            Arguments.of("/**/path1/{pv1}", "/path1/pv1/path2", Set.of("/path2")),
            Arguments.of("/**/path2/{pv1}", "/path1/path2/pv1", Set.of("")),
            Arguments.of("/**/path2/{pv1}", "/path1/path2/pv1/path3", Set.of("/path3")),
            Arguments.of("/**/{pv1}/path1/path2", "/pv1/path1/path2", Set.of("")),
            Arguments.of("/**/{pv1}/path1/path2", "/pv1/path1/path2/path3", Set.of("/path3")),
            Arguments.of("/**/{pv1}/path1/path2", "/pv1/path1/path1/path2", Set.of("")),
            Arguments.of("/**/{pv1}/path1/path1", "/pv1/pv2/path1/path1/path2", Set.of("/path2")),
            Arguments.of("/**/{pv1}/path1/path2", "/pv1/pv2/path1/path1/path2", Set.of("")),
            Arguments.of("/**/{pv1}/path1/path2", "/pv1/pv2/pv3/path1/path1/path2", Set.of("")),
            Arguments.of("/**/path1/{pv1}/path2", "/path1/pv1/path2", Set.of("")),
            Arguments.of("/**/path1/path2/{pv1}", "/path1/path2/pv1", Set.of("")),
            Arguments.of("/**/{pv1}/{pv2}/path1", "/pv1/pv2/pv3/path1", Set.of("")),
            Arguments.of("/**/{pv1}/path1/{pv2}", "/pv1/path1/pv2", Set.of("")),
            Arguments.of("/**/{pv1}/path1/{pv2}", "/pv1/path1/pv2/path2", Set.of("/path2")),
            Arguments.of("/**/path1/{pv1}/{pv2}", "/path1/pv1/pv2", Set.of("")),
            Arguments.of("/**/path1/{pv1}/{pv2}", "/path1/pv1/pv2/path2", Set.of("/path2")),
            Arguments.of("/**/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3", Set.of("")),
            Arguments.of("/**/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3/pv4", Set.of("", "/pv4")),
            Arguments.of("/**/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3/pv4/pv5", Set.of("", "/pv5", "/pv4/pv5"))
        );
    }

    @DisplayName("match 가능하면, 매칭된 pathVariable 을 가져옵니다.")
    @ParameterizedTest
    @MethodSource("providePathAndExpectPathVariable")
    void test4(String methodPath, String requestPath, Set<RequestValues> expectPathVariable) throws Exception {
        //given
        SegmentsMatcher matcher = new SegmentsMatcher(methodPath);

        //when
        List<MatchResult> match = matcher.match(requestPath);
        Set<RequestValues> actual = match.stream()
            .map(MatchResult::getPathVariable)
            .collect(Collectors.toUnmodifiableSet());

        //then
        Assertions.assertThat(actual).isEqualTo(expectPathVariable);
    }

    public static Stream<Arguments> providePathAndExpectPathVariable() {
        return Stream.of(
            Arguments.of("/{pv1}", "/pv1",
                         Set.of(new RequestValues(Map.of("pv1", "pv1")))
            ),
            Arguments.of("/{pv1}", "/pv1/pv2",
                         Set.of(new RequestValues(Map.of("pv1", "pv1")))
            ),
            Arguments.of("/{pv1}", "/pv1/pv2/pv3",
                         Set.of(new RequestValues(Map.of("pv1", "pv1")))
            ),
            Arguments.of("/{pv1}/{pv2}", "/pv1/pv2",
                         Set.of(new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2")))
            ),
            Arguments.of("/{pv1}/{pv2}", "/pv1/pv2/pv3",
                         Set.of(new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2")))
            ),
            Arguments.of("/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3",
                         Set.of(new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2", "pv3", "pv3")))
            ),
            Arguments.of("/**/{pv1}", "/pv1",
                         Set.of(new RequestValues(Map.of("pv1", "pv1")))
            ),
            Arguments.of("/**/{pv1}/path1", "/pv1/path1",
                         Set.of(new RequestValues(Map.of("pv1", "pv1")))
            ),
            Arguments.of("/**/{pv1}/path1", "/pv1/path1/path2",
                         Set.of(new RequestValues(Map.of("pv1", "pv1")))
            ),
            Arguments.of("/**/{pv1}/{pv2}/path1", "/pv1/pv2/pv3/path1",
                         Set.of(new RequestValues(Map.of("pv1", "pv2", "pv2", "pv3")))
            ),
            Arguments.of("/**/{pv1}/path1/{pv2}", "/pv1/path1/pv2",
                         Set.of(new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2")))
            ),
            Arguments.of("/**/{pv1}/path1/{pv2}", "/pv1/path1/pv2/path2",
                         Set.of(new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2")))
            ),
            Arguments.of("/**/path1/{pv1}/{pv2}", "/path1/pv1/pv2",
                         Set.of(new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2")))
            ),
            Arguments.of("/**/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3",
                         Set.of(new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2", "pv3", "pv3")))
            ),

            Arguments.of("/**/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3/pv4",
                         Set.of(new RequestValues(Map.of("pv1", "pv2", "pv2", "pv3", "pv3", "pv4")),
                                new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2", "pv3", "pv3")))
            ),
            Arguments.of("/**/{pv1}/{pv2}/{pv3}", "/pv1/pv2/pv3/pv4/pv5",
                         Set.of(new RequestValues(Map.of("pv1", "pv3", "pv2", "pv4", "pv3", "pv5")),
                                new RequestValues(Map.of("pv1", "pv2", "pv2", "pv3", "pv3", "pv4")),
                                new RequestValues(Map.of("pv1", "pv1", "pv2", "pv2", "pv3", "pv3"))
                         )
            )
        );
    }


}