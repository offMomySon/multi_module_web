package request;

import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class UriTest {

    @DisplayName("path 가 project 의 디폴트 경로 하위를 가리키면 exception 이 발생합니다.")
    @ParameterizedTest
    @CsvSource(value = {
        "/../,/",
        "/../path1/apth2,/path1/apth2",
        "/path1/../../,/",
        "/path1/path2/../../../,/",
        "/path1/path2/path3/../../../../,/",
        "/path1/../../path2/../,/",
        "/path1/path2/../../../path3/../,/",
        "/path1/path2/../../../path3/path4/../,/path3",
        "/path1/path2/path3/../../../../path4/path5/../,/path4",
        "/path1/../path2/../path3/../../path4/path5/../,/path4",
        "/path1/../../path2/../path3/../path4/path5/../,/path4",
        "/path1/../path2/../path3/../path4/../../path4/path5/../,/path4",
        "/path1/./../../,/",
        "/path1/path2/./../../../,/",
        "/path1/path2/path3/././../../../../,/",
        "/path1/././../../path2/../,/",
        "/path1/path2/././../../../path3/../,/",
        "/path1/path2/././../../../path3/path4/../,/path3",
        "/path1/path2/path3/./../../../../path4/path5/../,/path4",
        "/path1/./../path2/./../path3/../../path4/path5/../,/path4",
        "/path1/../../path2/../path3/../path4/path5/../,/path4",
        "/path1/./../path2/./../path3/./../path4/./../../path4/path5/../,/path4",
        "/path1/../../path2/../../path3/./../path4/./../../path5/path6/../,/path5",
    }, delimiter = ',')
    void test1(String _doesNotNormalizedPath, String _normalizedPath) {
        //given
        Uri uriFromDoesNotNormalizedPath = Uri.from(_doesNotNormalizedPath);
        Uri uriFromNormalizedPath = Uri.from(_normalizedPath);

        //when
        boolean actual = Objects.equals(uriFromDoesNotNormalizedPath, uriFromNormalizedPath);

        //then
        Assertions.assertThat(actual)
            .isTrue();
    }

    @DisplayName("uri 에 특수문자가 존재하지 않는 path 이면 FilePath 를 생성합니다.")
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
        "/path1/../path2/../path3/path4/path5",
        "/path1/../path2/../path3/path4/path5?queryWithSpecialCharacter=`",
        "/path1/../path2/../path3/path4/path5?queryWithSpecialCharacter=`~!",
        "/path1/../path2/../path3/path4/path5?queryWithSpecialCharacter=\"!@#$%&*()'+,-.:;<=>?[]^_`{|}\\"
    })
    void test2(String value) {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> Uri.from(value));

        //then
        Assertions.assertThat(actual)
            .isNull();
    }

    @DisplayName("query 를 제외한 uri 에 허용되지 않은 특수문자를 포함하고 있으면 exeception 이 발생합니다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "/`",
        "/$!\n",
        "/$!\\",
        "/]sdf",
        "/\"!@#$%&*()'+,-.:;<=>?[]^_`{|}\\",
        "/]sdf?test=test",
    })
    void test3(String path) {
        //given
        //when
        Throwable actual = Assertions.catchThrowable(() -> Uri.from(path));

        //then
        System.out.println();
        Assertions.assertThat(actual)
            .isInstanceOf(RuntimeException.class);
    }
}