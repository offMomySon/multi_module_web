package mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {

    @DisplayName("enclose")
    @Test
    void test() throws Exception {
        //given
        //when
        boolean actual = PathUtils.enclosedBy("{path}","{", "}");

        //then
        Assertions.assertThat(actual).isTrue();
    }

}