//package vo;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//@Disabled
//class HttpMethodTest {
//
//    @ParameterizedTest
//    @DisplayName("일치하는 method 가 존재하지 않으면 empty value 를 반환합니다.")
//    @ValueSource(strings = {
//        "notExistGET",
//        "GETNotExist",
//        "notExistPOST",
//        "POSTNotExist",
//        "notExistPUT",
//        "PUTNotExist",
//        "notExistPATCH",
//        "PATCHNotExist",
//        "notExistDELETE",
//        "DELETENotExist"
//    })
//    void test(String sMethod){
//        //given
//
//        //when
//        Throwable actual = Assertions.catchThrowable(() -> {
//            vo.HttpMethod.find(sMethod);
//        });
//
//        //then
//        Assertions.assertThat(actual)
//            .isNotNull();
//    }
//
//    @ParameterizedTest
//    @DisplayName("일치하는 method 가 존재하면 method 를 반환합니다.")
//    @ValueSource(strings = {
//        "GET", "gET", "geT",
//        "POST", "POSt", "POst", "POsT",
//        "PUT", "puT", "PuT", "Put",
//        "PATCH", "PAtCH", "PAtcH", "PAtch", "PaTcH",
//        "DELETE", "DELEtE", "DELete", "DelETe"
//    })
//    void test1(String sMethod){
//        //given
//        //when
//        vo.HttpMethod actual = vo.HttpMethod.find(sMethod);
//
//        //then
//        Assertions.assertThat(actual)
//            .isNotNull();
//    }
//}