package mapper.segmentv3;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PathUrlTest {
    @DisplayName("PathUrl 을 생성합니다.")
    @Test
    void test() throws Exception {
        //given
        PathUrl expect = new PathUrl(new StringBuilder("test/p1/p2"), 0);

        //when
        PathUrl actual = PathUrl.from("/test/p1/p2");

        //then
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("coypy 동작을 수행합니다.")
    @Test
    void ttest() throws Exception {
        //given
        PathUrl expect = PathUrl.from("/test/p1/p2");

        //when
        PathUrl actual = expect.copy();

        //then
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("첫번째 segment 를 pop 합니다.")
    @Test
    void tttest() throws Exception {
        //given
        PathUrl pathUrl = PathUrl.from("/test/p1/p2");

        //when
        List<String> actual = new ArrayList<>();
        while (pathUrl.doesNotEmpty()) {
            String segment = pathUrl.popSegment();
            System.out.println(segment);
            actual.add(segment);
        }

        //then
        assertThat(actual).containsSequence("test", "p1", "p2");
    }

}