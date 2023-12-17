package matcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import matcher.PathMatcher.MatchedElement;
import matcher.path.PathUrl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static matcher.PathMatcher.Token;
import static matcher.PathMatcher.empty;

class PathMatcherTest {

    @DisplayName("빈 pathMatcher 를 생성한다.")
    @Test
    void given_when_empty_then_createEmptyPathMatcher() throws Exception {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(PathMatcher::empty);

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("기존 pathMatcher 에 element 를 추가한다.")
    @Test
    void given_when_add_element_then_success() throws Exception {
        //given
        PathMatcher<Object> pathMatcher = empty();

        //when
        Throwable actual = Assertions.catchThrowable(() -> {
            Token token = new Token("test");
            PathUrl pathUrl = PathUrl.of("/test");
            pathMatcher.add(token, pathUrl, "test");
        });

        //then
        Assertions.assertThat(actual).isNull();
    }

    @DisplayName("token, url 에 일치하는 값을 찾으면 값을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {
        "/path, /path, true",
        "/path, /nonExist/path, false",
    })
    void given_pathMatcher_when_match_then_foundElement(String baseUrl, String baseFindUrl, boolean isPresent) throws Exception {
        //given
        Token token = new Token("toekn");
        PathUrl pathUrl = PathUrl.of(baseUrl);
        PathMatcher<String> pathMatcher = PathMatcher.empty();
        pathMatcher = pathMatcher.add(token, pathUrl, "element");

        PathUrl findUrl = PathUrl.of(baseFindUrl);

        //when
        Optional<MatchedElement<String>> optionalMatchedElement = pathMatcher.match(token, findUrl);

        //then
        Assertions.assertThat(optionalMatchedElement.isPresent()).isEqualTo(isPresent);
    }

    @DisplayName("pathMatcher 간의 결합을 수행한다.")
    @Test
    void given_pathMatcher_when_concat_then_concatedPathMatcher() throws Exception {
        //given
        List<Token> tokens = List.of(new Token("token1"), new Token("token2"));
        List<PathUrl> pathUrls = Stream.of("/url1", "/url2").map(PathUrl::of).collect(Collectors.toUnmodifiableList());

        List<PathMatcher<String>> pathMatchers = tokens.stream()
            .map(token -> pathUrls.stream()
                .reduce(PathMatcher.<String>empty(),
                        (pm, pathurl) -> pm.add(token, pathurl, "element"),
                        PathMatcher::concat))
            .collect(Collectors.toUnmodifiableList());

        //when
        PathMatcher<String> concatedPathMatcher = pathMatchers.stream().reduce(empty(), PathMatcher::concat);

        //then
        List<Optional<MatchedElement<String>>> optionalMatchedElements = tokens.stream()
            .flatMap(token -> pathUrls.stream()
                .map(url -> concatedPathMatcher.match(token, url))
            )
            .collect(Collectors.toUnmodifiableList());

        Assertions.assertThat(optionalMatchedElements)
            .allMatch(Optional::isPresent);
    }
}