package mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PathTest {


    @DisplayName("")
    @Test
    void test() throws Exception {
        //given
        Path p = new Path("ttt");
        Path p2 = new Path("ttt");

        //when
        boolean match = p.match(p2);
        System.out.println(match);

        //then

    }
}