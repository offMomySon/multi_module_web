package request;

import java.net.URISyntaxException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FilePathTest {

    @DisplayName("path 가 project 의 디폴트 경로 하위를 가리키면 exception 이 발생합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/../",
        "/../path1/apth2",
        "/path1/../../",
        "/path1/path2/../../../",
        "/path1/path2/path3/../../../../",
        "/path1/../../path2/../",
        "/path1/path2/../../../path3/../",
        "/path1/path2/../../../path3/path4/../",
        "/path1/path2/path3/../../../../path4/path5/../",
        "/path1/../path2/../path3/../../path4/path5/../",
        "/path1/../../path2/../path3/../path4/path5/../",
        "/path1/../path2/../path3/../path4/../../path4/path5/../",
        "/path1/./../../",
        "/path1/path2/./../../../",
        "/path1/path2/path3/././../../../../",
        "/path1/././../../path2/../",
        "/path1/path2/././../../../path3/../",
        "/path1/path2/././../../../path3/path4/../",
        "/path1/path2/path3/./../../../../path4/path5/../",
        "/path1/./../path2/./../path3/../../path4/path5/../",
        "/path1/../../path2/../path3/../path4/path5/../",
        "/path1/./../path2/./../path3/./../path4/./../../path4/path5/../",
        "/path1/../../path2/../../path3/./../path4/./../../path5/path6/../",
    })
    void test1(String value) {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> FilePath.of(value));

        //then
        Assertions.assertThat(actual)
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("올바른 path 이면 FilePath 를 생성합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        ".",
        "/./",
        "/path1/",
        "/path1/path2/..",
        "/path1/path2/path3/../..",
        "/path1/..",
        "/path1/path2/../..",
        "/path1/path2/path3/../../..",
        "/path1/../path2/../",
        "/path1/../path2/../path3/..",
        "/path1/../path2/../path3/../path4/..",
        "/path1/../path2/../path3",
        "/path1/../path2/../path3/path4",
        "/path1/../path2/../path3/path4/path5"
    })
    void test2(String value){
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> FilePath.of(value));

        //then
        Assertions.assertThat(actual)
            .isNull();
    }

    @DisplayName("허용되지 않은 특수문자를 포함한 path 이면 exeception 이 발생합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/`",
        "/$!\n",
        "/$!\\",
        "/]sdf",
    })
    void test3(String value) {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(()->FilePath.of(value));

        //then
        Assertions.assertThat(actual)
            .isInstanceOf(IllegalArgumentException.class);
    }
}