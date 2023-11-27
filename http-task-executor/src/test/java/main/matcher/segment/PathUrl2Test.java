package main.matcher.segment;

import matcher.segment.PathUrl2;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PathUrl2Test {
    @DisplayName("PathUrl 을 생성합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl2 expect = new PathUrl2(new StringBuilder("test/p1/p2"), 0);

        //when
        PathUrl2 actual = PathUrl2.from("/test/p1/p2");

        //then
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("coypy 동작을 수행합니다.")
    @Test
    void ttest() throws Exception {
        //given
        PathUrl2 expect = PathUrl2.from("/test/p1/p2");

        //when
        PathUrl2 actual = expect.copy();

        //then
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("첫번째 segment 를 pop 합니다.")
    @Test
    void tttest() throws Exception {
        //given
        PathUrl2 pathUrl = PathUrl2.from("/test/p1/p2");

        //when
        List<String> actual = new ArrayList<>();
        while (pathUrl.doesNotEmpty()) {
            String segment = pathUrl.popSegment();
            actual.add(segment);
        }

        //then
        assertThat(actual).containsSequence("test", "p1", "p2");
    }

    @DisplayName("첫번째 segment 를 peek 합니다.")
    @Test
    void ttttest() throws Exception {
        //given
        PathUrl2 pathUrl = PathUrl2.from("/test/p1/p2");

        //when
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String segment = pathUrl.peekSegment();
            actual.add(segment);
        }

        //then
        assertThat(actual).containsOnly("test");
    }

    @DisplayName("PathUrl 의 segment size 를 가져옵니다.")
    @Test
    void tttttest() throws Exception {
        //given
        PathUrl2 pathUrl = new PathUrl2(new StringBuilder("test/p1/p2"), "test/p1/".length());

        //when
        int size = pathUrl.segmentSize();

        //then
        assertThat(size).isEqualTo(1);
    }

    @DisplayName("PathUrl 을 list 형태로 복사합니다.")
    @Test
    void ttttttest() throws Exception {
        //given
        PathUrl2 pathUrl = new PathUrl2(new StringBuilder("p1/p2/p3"), "p1/".length());
        List<String> expect = List.of("p2", "p3");

        //when
        List<String> actual = pathUrl.toList();

        //then
        assertThat(actual).containsSequence(expect);
    }

    @DisplayName("비어있는 pathUrl 을 생성합니다.")
    @Test
    void tttttttest() throws Exception {
        //given
        PathUrl2 emptyPathUrl = PathUrl2.empty();

        //when
        boolean actual = emptyPathUrl.isEmpty();

        //then
        assertThat(actual).isTrue();
    }
}